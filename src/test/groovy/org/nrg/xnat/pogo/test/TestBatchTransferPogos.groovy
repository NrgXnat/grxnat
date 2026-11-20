package org.nrg.xnat.pogo.test

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.transfer.BatchTransferPayload
import org.nrg.xnat.pogo.transfer.ManifestRowStatus
import org.nrg.xnat.pogo.transfer.ManifestValidationRequest
import org.nrg.xnat.pogo.transfer.ManifestValidationResult
import org.nrg.xnat.pogo.transfer.TransferCapabilities
import org.nrg.xnat.pogo.transfer.TransferItemStatus
import org.nrg.xnat.pogo.transfer.TransferMode
import org.nrg.xnat.pogo.transfer.TransferRequest
import org.nrg.xnat.pogo.transfer.TransferRunDetail
import org.nrg.xnat.pogo.transfer.TransferRunItem
import org.nrg.xnat.pogo.transfer.TransferRunPage
import org.nrg.xnat.pogo.transfer.TransferRunStatus
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertNotEquals
import static org.testng.Assert.assertNull
import static org.testng.Assert.assertTrue

/**
 * The Batch Transfer pogos against the plugin's wire format: snake_case names, modes and statuses by their API values,
 * dates as epoch milliseconds, and fields the plugin adds later ignored.
 */
class TestBatchTransferPogos {

    private static final ObjectMapper MAPPER = XnatInterface.XNAT_REST_MAPPER

    @Test
    void testPayloadSerialization() {
        final BatchTransferPayload payload = new BatchTransferPayload().
                trackingId('batch_transfer_1').
                sourceProject('SRC').
                anonScript('version "6.3"\n(0010,0020) := "{csv.pid}"').
                anonReplacePipeline(true).
                addRequest(new TransferRequest(TransferMode.REIMPORT, 'XNAT_E00001', 'DST').
                        preserveSubjectLabel(false).
                        preserveSessionLabel(true).
                        destinationSubjectLabel('SUBJ_1').
                        destinationSessionLabel('SESS_1').
                        csvValues([pid: 'P1', patientName: 'Name'])).
                addRequest(new TransferRequest(TransferMode.SHARE, 'XNAT_E00002', 'DST'))

        final JsonNode json = MAPPER.readTree(MAPPER.writeValueAsString(payload))
        assertEquals(json.fieldNames().toList().toSet(), ['tracking_id', 'source_project', 'anon_script', 'anon_replace_pipeline', 'requests'] as Set)
        assertEquals(json.get('tracking_id').asText(), 'batch_transfer_1')
        assertTrue(json.get('anon_replace_pipeline').asBoolean())

        final JsonNode reimport = json.get('requests').get(0)
        assertEquals(reimport.get('id').asText(), 'XNAT_E00001')
        assertEquals(reimport.get('mode').asText(), 'Reimport')
        assertEquals(reimport.get('destination_project').asText(), 'DST')
        assertFalse(reimport.get('preserve_subject_label').asBoolean())
        assertTrue(reimport.get('preserve_session_label').asBoolean())
        assertEquals(reimport.get('destination_subject_label').asText(), 'SUBJ_1')
        assertEquals(reimport.get('destination_session_label').asText(), 'SESS_1')
        // Map keys are the manifest's column names, so the naming strategy must leave them alone.
        assertEquals(reimport.get('csv_values').fieldNames().toList(), ['pid', 'patientName'])

        final JsonNode share = json.get('requests').get(1)
        assertEquals(share.fieldNames().toList().toSet(), ['id', 'mode', 'destination_project'] as Set)
        assertEquals(share.get('mode').asText(), 'Share')
    }

    @Test
    void testPayloadWithoutScriptOmitsScriptFields() {
        final BatchTransferPayload payload = new BatchTransferPayload(requests: [new TransferRequest(TransferMode.CLONE, 'XNAT_S00001', 'DST')])
        final JsonNode json = MAPPER.readTree(MAPPER.writeValueAsString(payload))
        assertEquals(json.fieldNames().toList(), ['requests'])
        assertEquals(json.get('requests').get(0).get('mode').asText(), 'Clone')
    }

    @Test
    void testManifestValidationRequestSerialization() {
        final ManifestValidationRequest request = new ManifestValidationRequest().
                sourceProject('SRC').
                manifestCsv('source_subject_label,source_session_label\nS1,S1_MR1\n').
                fileCount(true)
        final JsonNode json = MAPPER.readTree(MAPPER.writeValueAsString(request))
        assertEquals(json.fieldNames().toList().toSet(), ['source_project', 'manifest_csv', 'file_count'] as Set)
        assertTrue(json.get('file_count').asBoolean())
    }

    @Test
    void testNewTrackingId() {
        final String trackingId = BatchTransferPayload.newTrackingId()
        assertTrue(trackingId.startsWith(BatchTransferPayload.TRACKING_ID_PREFIX))
        assertNotEquals(BatchTransferPayload.newTrackingId(), trackingId)
    }

    @Test
    void testRunDetailDeserialization() {
        final String json = '''{
            "run": {
                "tracking_id": "batch_transfer_1",
                "status": "Warnings",
                "operation": "Reimport",
                "source_project": "SRC",
                "destination_project": "DST",
                "item_count": 2,
                "passed_count": 1,
                "failed_count": 1,
                "stopped_count": 0,
                "custom_script": true,
                "anon_replaced_pipeline": false,
                "preserve_session_label": true,
                "created_by": "admin",
                "created": 1763650000000,
                "finished": 1763650060000,
                "added_in_a_later_plugin": "ignored"
            },
            "can_stop": false,
            "anon_script": "version \\"6.3\\"",
            "items": [
                {
                    "item_id": "XNAT_E00001",
                    "mode": "Reimport",
                    "source_subject_label": "S1",
                    "source_label": "S1_MR1",
                    "destination_project": "DST",
                    "csv_values": {"pid": "P1"},
                    "status": "Done",
                    "destination_id": "XNAT_E00003",
                    "destination_label": "S1_MR1",
                    "started": 1763650001000,
                    "finished": 1763650030000
                },
                {
                    "item_id": "XNAT_E00002",
                    "mode": "Reimport",
                    "row_number": 3,
                    "destination_project": "DST",
                    "status": "Failed",
                    "message": "An experiment with the same label (S2_MR1) already exists in project DST."
                },
                {
                    "item_id": "XNAT_E00004",
                    "mode": "Share",
                    "status": "Done",
                    "destination_hidden": true
                }
            ],
            "items_as_of": 1763650030000
        }'''

        final TransferRunDetail detail = MAPPER.readValue(json, TransferRunDetail)
        assertEquals(detail.run.trackingId, 'batch_transfer_1')
        assertEquals(detail.run.status, TransferRunStatus.WARNINGS)
        assertTrue(detail.run.status.finished)
        assertEquals(detail.run.itemCount, 2)
        assertEquals(detail.run.failedCount, 1)
        assertTrue(detail.run.customScript)
        assertTrue(detail.run.preserveSessionLabel)
        assertNull(detail.run.preserveSubjectLabel)
        assertEquals(detail.run.created, new Date(1763650000000L))
        assertEquals(detail.run.finished, new Date(1763650060000L))
        assertFalse(detail.canStop)
        assertEquals(detail.anonScript, 'version "6.3"')
        assertEquals(detail.itemsAsOf, 1763650030000L)

        final TransferRunItem done = detail.findItem('XNAT_E00001')
        assertEquals(done.mode, TransferMode.REIMPORT)
        assertEquals(done.status, TransferItemStatus.DONE)
        assertEquals(done.destinationId, 'XNAT_E00003')
        assertEquals(done.csvValues, [pid: 'P1'])
        assertEquals(done.started, new Date(1763650001000L))

        final TransferRunItem failed = detail.findItem('XNAT_E00002')
        assertEquals(failed.rowNumber, 3)
        assertEquals(detail.itemsWithStatus(TransferItemStatus.FAILED), [failed])
        assertTrue(detail.findItem('XNAT_E00004').destinationHidden)
        assertNull(detail.findItem('XNAT_E00005'))

        final String summary = detail.summary()
        assertTrue(summary.startsWith('Transfer run batch_transfer_1 is Warnings: 1 passed, 1 failed, 0 stopped of 2.'), summary)
        assertTrue(summary.contains('Reimport XNAT_E00002 -> DST: Failed (An experiment with the same label (S2_MR1) already exists in project DST.)'), summary)
    }

    @Test
    void testRunPageDeserialization() {
        final TransferRunPage page = MAPPER.readValue('''{
            "runs": [{"tracking_id": "batch_transfer_2", "status": "Running", "operation": "Mixed", "item_count": 4}],
            "total": 26,
            "page": 1,
            "size": 25
        }''', TransferRunPage)
        assertEquals(page.total, 26L)
        assertEquals(page.page, 1)
        assertEquals(page.runs*.trackingId, ['batch_transfer_2'])
        assertEquals(page.runs[0].status, TransferRunStatus.RUNNING)
        assertFalse(page.runs[0].status.finished)
        assertEquals(page.runs[0].operation, 'Mixed')
    }

    @Test
    void testStatusesThatEndARun() {
        assertEquals(TransferRunStatus.values().findAll { it.finished } as Set,
                [TransferRunStatus.COMPLETED, TransferRunStatus.WARNINGS, TransferRunStatus.FAILED, TransferRunStatus.STOPPED] as Set)
        assertEquals(TransferItemStatus.values().findAll { it.finished } as Set,
                [TransferItemStatus.DONE, TransferItemStatus.FAILED, TransferItemStatus.STOPPED] as Set)
    }

    @Test
    void testManifestResultDeserialization() {
        final ManifestValidationResult result = MAPPER.readValue('''{
            "columns": ["source_subject_label", "source_session_label", "pid"],
            "required_present": true,
            "missing_columns": [],
            "reserved_columns": [],
            "routing_columns": [],
            "value_columns": ["pid"],
            "total_rows": 2,
            "summary": {"matched": 1, "not_found": 1, "no_sessions": 0, "duplicate_session": 0},
            "rows": [
                {"index": 1, "row_number": 2, "source_subject_label": "S1", "source_session_label": "S1_MR1", "subject_row": false,
                 "status": "matched", "resolved_id": "XNAT_E00001", "csv_values": {"pid": "P1"}, "value_errors": [], "value_warnings": []},
                {"index": 2, "row_number": 3, "source_subject_label": "S9", "subject_row": true,
                 "status": "subject_not_found", "csv_values": {"pid": "P9"}, "value_errors": [], "value_warnings": []}
            ],
            "script_binding": {"bound": false, "unbound": ["name"]}
        }''', ManifestValidationResult)
        assertTrue(result.requiredPresent)
        assertEquals(result.valueColumns, ['pid'])
        assertEquals(result.summary.matched, 1)
        assertEquals(result.summary.notFound, 1)
        assertEquals(result.rows*.status, [ManifestRowStatus.MATCHED, ManifestRowStatus.SUBJECT_NOT_FOUND])
        assertEquals(result.rows[0].resolvedId, 'XNAT_E00001')
        assertEquals(result.rows[1].rowNumber, 3)
        assertTrue(result.rows[1].subjectRow)
        assertNull(result.rows[0].fileCount)
        assertFalse(result.scriptBinding.bound)
        assertEquals(result.scriptBinding.unbound, ['name'])
    }

    @Test
    void testCapabilitiesDeserialization() {
        final TransferCapabilities capabilities = MAPPER.readValue('''{
            "per_import_anon": true,
            "anon_script_dialect": "DicomEdit 6",
            "placeholder_syntax": "${csv.<column>}",
            "manifest_max_rows": 5000,
            "max_anon_script_bytes": 65536,
            "anon_value_charset": "A-Za-z0-9 ._-"
        }''', TransferCapabilities)
        assertTrue(capabilities.perImportAnon)
        assertEquals(capabilities.anonScriptDialect, 'DicomEdit 6')
        assertEquals(capabilities.manifestMaxRows, 5000)
        assertEquals(capabilities.maxAnonScriptBytes, 65536)
    }

}
