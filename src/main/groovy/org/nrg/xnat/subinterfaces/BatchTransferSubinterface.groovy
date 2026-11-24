package org.nrg.xnat.subinterfaces

import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.apache.commons.lang3.time.StopWatch
import org.nrg.testing.TimeUtils
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.meta.RequirePlugin
import org.nrg.xnat.pogo.PluginRegistry
import org.nrg.xnat.pogo.transfer.BatchTransferPayload
import org.nrg.xnat.pogo.transfer.DestinationLabelsResult
import org.nrg.xnat.pogo.transfer.ManifestValidationRequest
import org.nrg.xnat.pogo.transfer.ManifestValidationResult
import org.nrg.xnat.pogo.transfer.ScriptValidationResult
import org.nrg.xnat.pogo.transfer.TransferCapabilities
import org.nrg.xnat.pogo.transfer.TransferRequest
import org.nrg.xnat.pogo.transfer.TransferRun
import org.nrg.xnat.pogo.transfer.TransferRunDetail
import org.nrg.xnat.pogo.transfer.TransferRunPage
import org.nrg.xnat.pogo.transfer.TransferRunStatus

import java.nio.charset.StandardCharsets

import static org.testng.AssertJUnit.assertEquals

/**
 * Client for the Batch Transfer plugin, 2.0.0 and later (plugin ID {@code batchTransferPlugin}): Share, Clone and
 * Reimport batches, the checks the plugin offers before a submit, run history and Stop, and the plugin's settings.
 * The older Batch Share plugin ({@code batchSharePlugin}) has its own client, {@link BatchShareSubinterface}.
 *
 * <p>The plugin runs a batch in the background. Submit it with {@link #launchBatchTransfer}, then wait for it with
 * {@link #waitForTransferRun}, which reads the run's history until the run finishes.
 */
@RequirePlugin(PluginRegistry.BATCH_TRANSFER_ID)
class BatchTransferSubinterface extends XnatPluginSubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/transfer',
                '/xapi/transfer/capabilities',
                '/xapi/transfer/validate/script',
                '/xapi/transfer/validate/manifest',
                '/xapi/transfer/validate/destination_labels',
                '/xapi/transfer/history',
                '/xapi/transfer/history/{tracking_id}',
                '/xapi/transfer/history/{tracking_id}/stop',
                '/xapi/batch_transfer/jms_queues'
        ]
    }

    TransferCapabilities readTransferCapabilities() {
        queryBase().get(formatXapiUrl('transfer', 'capabilities')).
                then().assertThat().statusCode(200).
                extract().as(TransferCapabilities)
    }

    /**
     * Submits a batch and returns its tracking ID. A payload without a tracking ID is given a new one first.
     */
    String launchBatchTransfer(BatchTransferPayload payload) {
        if (!payload.trackingId) {
            payload.trackingId = BatchTransferPayload.newTrackingId()
        }
        postTransfer(payload).assertThat().statusCode(200)
        payload.trackingId
    }

    String launchBatchTransfer(List<TransferRequest> requests, String anonScript = null) {
        launchBatchTransfer(new BatchTransferPayload(requests: requests, anonScript: anonScript))
    }

    /**
     * Submits a batch the plugin must refuse with the given status, and returns the response body, which gives the
     * reason.
     */
    String launchBatchTransferExpectingRejection(BatchTransferPayload payload, int expectedStatus) {
        postTransfer(payload).assertThat().statusCode(expectedStatus).extract().asString()
    }

    /**
     * Checks a custom script without submitting anything. With {@code manifestColumns}, the result also reports which
     * {@code ${csv.*}} placeholders those columns leave unbound; an empty list leaves every placeholder unbound.
     */
    ScriptValidationResult validateTransferScript(String anonScript, List<String> manifestColumns = null) {
        final Map<String, Object> body = [anon_script: anonScript]
        if (manifestColumns != null) {
            body.manifest_columns = manifestColumns
        }
        queryBase().contentType(ContentType.JSON).body(body).post(formatXapiUrl('transfer', 'validate', 'script')).
                then().assertThat().statusCode(200).
                extract().as(ScriptValidationResult)
    }

    ManifestValidationResult validateTransferManifest(ManifestValidationRequest request) {
        queryBase().contentType(ContentType.JSON).body(request).post(formatXapiUrl('transfer', 'validate', 'manifest')).
                then().assertThat().statusCode(200).
                extract().as(ManifestValidationResult)
    }

    /**
     * The multipart form of {@link #validateTransferManifest}: the manifest goes as an uploaded file, as it does from
     * the plugin's screen.
     */
    ManifestValidationResult validateTransferManifestUpload(String sourceProject, String manifestCsv, String anonScript = null, Boolean fileCount = null) {
        final RequestSpecification request = queryBase().
                multiPart('manifest', 'manifest.csv', manifestCsv.getBytes(StandardCharsets.UTF_8), 'text/csv').
                multiPart('source_project', sourceProject)
        if (anonScript != null) {
            request.multiPart('anon_script', anonScript)
        }
        if (fileCount != null) {
            request.multiPart('file_count', fileCount.toString())
        }
        request.post(formatXapiUrl('transfer', 'validate', 'manifest')).
                then().assertThat().statusCode(200).
                extract().as(ManifestValidationResult)
    }

    /** Of the given session labels, finds those a session in the destination project already has. */
    DestinationLabelsResult validateTransferDestinationLabels(String destinationProject, List<String> sessionLabels) {
        queryBase().contentType(ContentType.JSON).
                body([destination_project: destinationProject, session_labels: sessionLabels]).
                post(formatXapiUrl('transfer', 'validate', 'destination_labels')).
                then().assertThat().statusCode(200).
                extract().as(DestinationLabelsResult)
    }

    /**
     * Lists runs, newest first: a project's runs, as source or destination, or the caller's own runs when no project is
     * given. {@code all} lists every run, and only a site admin may ask for it.
     */
    TransferRunPage readTransferHistory(String project = null, TransferRunStatus status = null, Integer page = null, Integer size = null, Boolean all = null) {
        final Map<String, Object> params = [:]
        if (project != null) {
            params.project = project
        }
        if (status != null) {
            params.status = status.apiValue
        }
        if (page != null) {
            params.page = page
        }
        if (size != null) {
            params.size = size
        }
        if (all != null) {
            params.all = all
        }
        queryBase().queryParams(params).get(formatXapiUrl('transfer', 'history')).
                then().assertThat().statusCode(200).
                extract().as(TransferRunPage)
    }

    /**
     * Reads a run and its items. With {@code changedSince}, a previous response's {@code itemsAsOf}, only the items
     * changed since then are included. Throws a NotFoundException for an unknown tracking ID, and a
     * ForbiddenException when the caller can't see the run.
     */
    TransferRunDetail readTransferRun(String trackingId, Long changedSince = null) {
        final RequestSpecification request = queryBaseWithCommonStatusCodeListeners()
        if (changedSince != null) {
            request.queryParam('changed_since', changedSince)
        }
        request.get(formatXapiUrl('transfer', 'history', trackingId)).
                then().assertThat().statusCode(200).
                extract().as(TransferRunDetail)
    }

    /**
     * Waits for a run to finish and returns it, whatever its final status. The run is recorded when the batch is
     * submitted, so an unknown tracking ID fails at once rather than at the time limit.
     *
     * <p>Default arguments here are literals: XnatInterface's delegating copies of these methods evaluate them in
     * XnatInterface, where this class's constants don't resolve.
     */
    TransferRunDetail waitForTransferRun(String trackingId, int maxSeconds = 20 * 60, int pollingMillis = 2000) {
        final StopWatch stopWatch = TimeUtils.launchStopWatch()
        while (true) {
            TimeUtils.checkStopWatch(stopWatch, maxSeconds, "Transfer run ${trackingId} did not finish within ${maxSeconds} seconds.".toString())
            final TransferRunDetail detail = readTransferRun(trackingId)
            if (detail.run?.status?.finished) {
                return detail
            }
            TimeUtils.sleep(pollingMillis)
        }
    }

    /**
     * Waits for a run to finish and checks that it ended with the expected status. On a mismatch, the assertion
     * message lists every item's outcome.
     */
    TransferRunDetail waitForTransferRun(String trackingId, TransferRunStatus expectedStatus, int maxSeconds = 20 * 60, int pollingMillis = 2000) {
        final TransferRunDetail detail = waitForTransferRun(trackingId, maxSeconds, pollingMillis)
        assertEquals(detail.summary(), expectedStatus, detail.run.status)
        detail
    }

    /** Asks a run to stop: items that haven't started are skipped, and items already transferring finish. */
    TransferRun stopTransferRun(String trackingId) {
        postStop(trackingId).assertThat().statusCode(200).extract().as(TransferRun)
    }

    /** Asks a run to stop when the plugin must refuse with the given status, and returns the response body. */
    String stopTransferRunExpectingRejection(String trackingId, int expectedStatus) {
        postStop(trackingId).assertThat().statusCode(expectedStatus).extract().asString()
    }

    /** The plugin's site settings (queue concurrency, script and manifest limits), keyed by preference name. */
    Map<String, Object> readBatchTransferSettings() {
        prohibitNonadmin()
        queryBase().get(formatXapiUrl('batch_transfer', 'jms_queues')).
                then().assertThat().statusCode(200).
                extract().as(Map)
    }

    /**
     * Sets some of the plugin's site settings, keyed by preference name, such as {@code manifestMaxRows}. Changes to
     * queue concurrency only take effect after XNAT restarts.
     */
    XnatInterface updateBatchTransferSettings(Map<String, ?> settings) {
        prohibitNonadmin()
        final Map<String, String> body = settings.collectEntries { name, value ->
            [(name): value?.toString()]
        } as Map<String, String>
        queryBase().contentType(ContentType.JSON).body(body).post(formatXapiUrl('batch_transfer', 'jms_queues')).
                then().assertThat().statusCode(200)
        xnatInterface
    }

    private ValidatableResponse postTransfer(BatchTransferPayload payload) {
        queryBase().contentType(ContentType.JSON).body(payload).post(formatXapiUrl('transfer')).then()
    }

    private ValidatableResponse postStop(String trackingId) {
        queryBase().post(formatXapiUrl('transfer', 'history', trackingId, 'stop')).then()
    }

}
