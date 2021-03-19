package org.nrg.xnat.subinterfaces

import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.resources.ScanResource
import org.nrg.xnat.rest.SerializationUtils

import static org.nrg.testing.CommonStringUtils.formatUrl

class ScanSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{ASSESSED_ID}/scans',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{ASSESSED_ID}/scans/{SCAN_ID}'
        ]
    }

    String sessionScansUrl(Project project, Subject subject, ImagingSession session) {
        formatUrl(xnatInterface.subjectAssessorUrl(project, subject, session), 'scans')
    }

    String sessionScansUrl(ImagingSession session) {
        sessionScansUrl(session.primaryProject, session.subject, session)
    }

    String scanUrl(Project project, Subject subject, ImagingSession session, Scan scan) {
        if (scan.id == null) {
            throw new UnsupportedOperationException('scan.id cannot be null')
        }
        formatUrl(sessionScansUrl(project, subject, session), scan.id)
    }

    String scanUrl(Scan scan) {
        scanUrl(scan.session.primaryProject, scan.session.subject, scan.session, scan)
    }

    List<Scan> readScans(Project project, Subject subject, ImagingSession session) {
        jsonQuery().get(sessionScansUrl(project, subject, session)).jsonPath().getObject('ResultSet.Result', Scan[]).collect { scan ->
            if (xnatInterface.readResources) {
                scan.scanResources(xnatInterface.readResources(new ScanResource().project(project).subject(subject).subjectAssessor(session).scan(scan)))
            }
            scan.session(session)
        }
    }

    void populateAdditionalScanMetadata(Project project, Subject subject, ImagingSession session) {
        session.scans.each { scan ->
            readAdditionalScanMetadata(project, subject, session, scan)
        }
    }

    Scan readAdditionalScanMetadata(Project project, Subject subject, ImagingSession session, Scan scan) {
        scan.uid(jsonQuery().get(scanUrl(project, subject, session, scan)).jsonPath().getString('items[0].data_fields.UID'))
    }


    void createScan(Project project, Subject subject, ImagingSession session, Scan scan) {
        if (scan.xsiType == null) {
            throw new UnsupportedOperationException("scan must have an xsiType")
        }
        queryBase().queryParams(SerializationUtils.serializeToMap(scan)).put(scanUrl(project, subject, session, scan)).then().assertThat().statusCode(200)

        scan.scanResources.each { resource ->
            resource.project(project).subject(subject).subjectAssessor(session).scan(scan)
        }
        xnatInterface.uploadResources(scan.scanResources)
    }

    void updateScan(Project project, Subject subject, ImagingSession session, Scan scan) {
        queryBase().queryParams(SerializationUtils.serializeToMap(scan)).put(scanUrl(project, subject, session, scan)).then().assertThat().statusCode(200)
    }

    void updateScan(Scan scan) {
        updateScan(scan.session.primaryProject, scan.session.subject, scan.session, scan)
    }

    void deleteScan(Project project, Subject subject, ImagingSession session, Scan scan) {
        queryBase().queryParam('removeFiles', true).delete(scanUrl(project, subject, session, scan)).then().assertThat().statusCode(200)
    }

    void deleteScan(Scan scan) {
        queryBase().queryParam('removeFiles', true).delete(scanUrl(scan)).then().assertThat().statusCode(200)
    }

}
