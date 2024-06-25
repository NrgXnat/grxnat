package org.nrg.xnat.subinterfaces

import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import org.apache.commons.lang3.StringUtils
import org.hamcrest.Matchers
import org.nrg.testing.CommonStringUtils
import org.nrg.testing.DicomUtils
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.meta.RequirePlugin
import org.nrg.xnat.pogo.PluginRegistry
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.dicomweb.DicomModelObject
import org.nrg.xnat.pogo.dicomweb.DicomMultipartItem
import org.nrg.xnat.pogo.experiments.ImagingSession

import javax.imageio.ImageIO
import javax.mail.BodyPart
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource
import java.awt.image.BufferedImage
import java.util.function.BiFunction

import static org.testng.AssertJUnit.assertEquals
import static org.testng.AssertJUnit.fail

@RequirePlugin(PluginRegistry.OHIF_VIEWER_ID)
class OhifViewerSubinterface extends XnatPluginSubinterface {

    public static final String DICOM_WEB_SEARCH_JSON = 'application/dicom+json'
    public static final String MULTIPART_DICOM_TYPE = 'application/dicom'
    public static final String MULTIPART_OCTET_STREAM_TYPE = 'application/octet-stream'
    public static final String MULTIPART_JPEG_TYPE = 'image/jpeg'
    public static final String MULTIPART_RELATED = 'multipart/related'
    public static final String MULTIPART_RELATED_DICOM = "${MULTIPART_RELATED}; type=\"${MULTIPART_DICOM_TYPE}\""
    public static final String MULTIPART_RELATED_OCTET_STREAM = "${MULTIPART_RELATED}; type=\"${MULTIPART_OCTET_STREAM_TYPE}\""

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/viewerDicomweb/aets/{projectId}/{experimentId}/rs/instances',
                '/xapi/viewerDicomweb/aets/{projectId}/{experimentId}/rs/series',
                '/xapi/viewerDicomweb/aets/{projectId}/{experimentId}/rs/studies',
                '/xapi/viewerDicomweb/aets/{projectId}/{experimentId}/rs/studies/{studyUID}',
                '/xapi/viewerDicomweb/aets/{projectId}/{experimentId}/rs/studies/{studyUID}/instances',
                '/xapi/viewerDicomweb/aets/{projectId}/{experimentId}/rs/studies/{studyUID}/metadata',
                '/xapi/viewerDicomweb/aets/{projectId}/{experimentId}/rs/studies/{studyUID}/series',
                '/xapi/viewerDicomweb/aets/{projectId}/{experimentId}/rs/studies/{studyUID}/series/{seriesUID}',
                '/xapi/viewerDicomweb/aets/{projectId}/{experimentId}/rs/studies/{studyUID}/series/{seriesUID}/instances',
                '/xapi/viewerDicomweb/aets/{projectId}/{experimentId}/rs/studies/{studyUID}/series/{seriesUID}/instances/{sopUID}',
                '/xapi/viewerDicomweb/aets/{projectId}/{experimentId}/rs/studies/{studyUID}/series/{seriesUID}/instances/{sopUID}/bulkdata/**',
                '/xapi/viewerDicomweb/aets/{projectId}/{experimentId}/rs/studies/{studyUID}/series/{seriesUID}/instances/{sopUID}/frames/{frameList}',
                '/xapi/viewerDicomweb/aets/{projectId}/{experimentId}/rs/studies/{studyUID}/series/{seriesUID}/instances/{sopUID}/metadata',
                '/xapi/viewerDicomweb/aets/{projectId}/{experimentId}/rs/studies/{studyUID}/series/{seriesUID}/metadata',
                '/xapi/viewerDicomweb/generate-all-dicomweb',
                '/xapi/viewerDicomweb/projects/{projectId}',
                '/xapi/viewerDicomweb/projects/{projectId}/experiments/{experimentId}',
                '/xapi/viewerDicomweb/projects/{projectId}/experiments/{experimentId}/exists',
                '/xapi/viewerDicomweb/projects/{projectId}/subjects/{subjectId}'
        ]
    }

    List<DicomModelObject> viewerDicomwebSearchInstances(Project project, ImagingSession session, String studyInstanceUid = null, String seriesInstanceUid = null) {
        searchTransaction([
                dicomWebBase(project, session),
                commonDicomPath(studyInstanceUid, seriesInstanceUid),
                '/instances'
        ])
    }

    List<DicomModelObject> viewerDicomwebSearchSeries(Project project, ImagingSession session, String studyInstanceUid = null) {
        searchTransaction([
                dicomWebBase(project, session),
                commonDicomPath(studyInstanceUid),
                '/series'
        ])
    }

    List<DicomModelObject> viewerDicomwebSearchStudies(Project project, ImagingSession session) {
        searchTransaction([
                dicomWebBase(project, session),
                '/studies'
        ])
    }

    List<DicomModelObject> viewerDicomwebRetrieveMetadata(Project project, ImagingSession session, String studyInstanceUid, String seriesInstanceUid = null, String sopInstanceUid = null) {
        viewerDicomwebRequestBase()
                .accept(DICOM_WEB_SEARCH_JSON)
                .get(
                        formDicomwebUrl([
                                dicomWebBase(project, session),
                                commonDicomPath(studyInstanceUid, seriesInstanceUid, sopInstanceUid),
                                '/metadata'
                        ])
                ).then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(DicomModelObject[])
    }

    List<DicomMultipartItem> viewerDicomwebRetrieveInstances(Project project, ImagingSession session, String studyInstanceUid, String seriesInstanceUid = null, String sopInstanceUid = null) {
        handleMultipart(
                viewerDicomwebRequestBase()
                        .accept(MULTIPART_RELATED_DICOM)
                        .get(
                                formDicomwebUrl([
                                        dicomWebBase(project, session),
                                        commonDicomPath(studyInstanceUid, seriesInstanceUid, sopInstanceUid)
                                ])
                        ),
                MULTIPART_DICOM_TYPE,
                { Map<String, String> contentType, InputStream inputStream ->
                    final DicomMultipartItem dicomItem = new DicomMultipartItem().dicom(DicomUtils.readDicom(inputStream))
                    final String reportedTransferSyntax = contentType['transfer-syntax']
                    // assertEquals(reportedTransferSyntax, dicomItem.dicom.getString(Tag.TransferSyntaxUID))
                    // TODO: restore original other checks
                    dicomItem.reportedTsUid(reportedTransferSyntax)
                    dicomItem
                }
        )
    }

    List<BufferedImage> viewerDicomwebRetrieveFrames(Project project, ImagingSession session, String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid, List<Integer> frames) {
        handleMultipart(
                viewerDicomwebRequestBase()
                        .accept(MULTIPART_RELATED_OCTET_STREAM)
                        .get(
                                formDicomwebUrl([
                                        dicomWebBase(project, session),
                                        commonDicomPath(studyInstanceUid, seriesInstanceUid, sopInstanceUid),
                                        '/frames',
                                        frames.join(',')
                                ])
                        ),
                MULTIPART_OCTET_STREAM_TYPE, // TODO: this can be jpeg
                { Map<String, String> contentType, InputStream inputStream ->
                    ImageIO.read(inputStream)
                }
        )
    }

    InputStream viewerDicomwebRetrieveBulkdata(String bulkDataUri) {
        handleMultipart(
                viewerDicomwebRequestBase()
                        .accept(MULTIPART_RELATED_OCTET_STREAM)
                        .get(bulkDataUri),
                MULTIPART_OCTET_STREAM_TYPE,
                { Map<String, String> contentType, InputStream inputStream ->
                    inputStream
                }
        )[0]
    }

    InputStream viewerDicomwebRetrieveBulkdata(Project project, ImagingSession session, String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid, List<String> dicomPath) {
        viewerDicomwebRetrieveBulkdata(
                formDicomwebUrl([
                        dicomWebBase(project, session),
                        commonDicomPath(studyInstanceUid, seriesInstanceUid, sopInstanceUid),
                        '/bulkdata',
                        dicomPath.join('/')
                ])
        )
    }

    XnatInterface viewerDicomwebGenerateData(boolean overwrite = false) {
        generateDicomwebData('/generate-all-dicomweb', overwrite)
    }

    XnatInterface viewerDicomwebGenerateDataForProject(Project project, boolean overwrite = false) {
        generateDicomwebData("/projects/${project.id}", overwrite)
    }

    XnatInterface viewerDicomwebGenerateDataForSubject(Project project, Subject subject, boolean overwrite = false) {
        generateDicomwebData("/projects/${project.id}/subjects/${xnatInterface.getAccessionNumber(subject)}", overwrite)
    }

    XnatInterface viewerDicomwebGenerateDataForSession(Project project, ImagingSession session, boolean overwrite = false) {
        generateDicomwebData("/projects/${project.id}/experiments/${xnatInterface.getAccessionNumber(session)}", overwrite)
    }

    boolean viewerDicomwebCheckMetadataExists(Project project, ImagingSession session) {
        final Response response = viewerDicomwebRequestBase()
                .get(formatXapiUrl(dicomWebUrl("/projects/${project.id}/experiments/${xnatInterface.getAccessionNumber(session)}/exists")))
        switch (response.statusCode()) {
            case 200:
                return true
            case 404:
                return false
            default:
                response.then().assertThat().statusCode(200)
        }
    }

    XnatInterface viewerDicomwebDeleteMetadata(Project project, ImagingSession session) {
        queryBaseWithCommonStatusCodeListeners()
                .delete(formatXapiUrl(dicomWebUrl("/projects/${project.id}/experiments/${xnatInterface.getAccessionNumber(session)}")))
                .then()
                .assertThat()
                .statusCode(200)
        xnatInterface
    }

    private String dicomWebBase(Project project, ImagingSession session) {
        dicomWebUrl("/aets/${project.id}/${xnatInterface.getAccessionNumber(session)}/rs")
    }

    private String dicomWebUrl(String additionalComponent) {
        CommonStringUtils.formatUrl('/viewerDicomweb', additionalComponent)
    }

    private String commonDicomPath(String studyInstanceUid = null, String seriesInstanceUid = null, String sopInstanceUid = null) {
        final List<String> urlComponents = []
        if (studyInstanceUid != null) {
            urlComponents << "/studies/${studyInstanceUid}".toString()
        }
        if (seriesInstanceUid != null) {
            urlComponents << "/series/${seriesInstanceUid}".toString()
        }
        if (sopInstanceUid != null) {
            urlComponents << "/instances/${sopInstanceUid}".toString()
        }
        urlComponents.join('')
    }

    private String formDicomwebUrl(List<String> urlComponents) {
        formatXapiUrl(urlComponents.findAll { StringUtils.isNotEmpty(it) } as String[])
    }

    private List<DicomModelObject> searchTransaction(List<String> urlComponents) {
        viewerDicomwebRequestBase()
                .accept(DICOM_WEB_SEARCH_JSON)
                .get(formDicomwebUrl(urlComponents))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(DicomModelObject[])
    }

    private <X> List<X> handleMultipart(Response response, String expectedType, BiFunction<Map<String, String>, InputStream, X> objectExtractor) {
        response.then().assertThat().statusCode(200)
        final BiFunction<String, String, Map<String, String>> validateAndParseContentType = { String typeValidation, String inputString ->
            final List<String> contentType = inputString.split(';')*.trim()
            assertEquals(typeValidation, contentType[0])
            contentType.tail().collectEntries { entry ->
                final List<String> parsed = entry.split('=')
                [(parsed[0]) : parsed[1]]
            }
        }

        assertEquals(
                expectedType,
                validateAndParseContentType.apply(MULTIPART_RELATED, response.contentType())['type']
        )

        final MimeMultipart multipart = new MimeMultipart(new ByteArrayDataSource(response.asInputStream(), expectedType))
        (0 ..< multipart.getCount()).collect { int index ->
            final BodyPart part = multipart.getBodyPart(index)
            objectExtractor.apply(
                    validateAndParseContentType.apply(expectedType, part.contentType),
                    part.inputStream
            )
        }
    }

    private XnatInterface generateDicomwebData(String urlComponent, boolean overwrite) {
        queryBaseWithCommonStatusCodeListeners()
                .queryParam('overwrite', overwrite)
                .post(formatXapiUrl(dicomWebUrl(urlComponent)))
                .then()
                .assertThat()
                .statusCode(Matchers.oneOf(200, 201))
        xnatInterface
    }

    private RequestSpecification viewerDicomwebRequestBase() {
        queryBaseWithStatusCodeListeners([FORBIDDEN_403, NOT_FOUND_404, UNPROCESSABLE_CONTENT_422])
    }

}
