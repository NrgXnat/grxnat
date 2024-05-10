package org.nrg.xnat.subinterfaces

import io.restassured.builder.MultiPartSpecBuilder
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import org.nrg.xnat.importer.ImportException
import org.nrg.xnat.importer.XnatArchivalRequest
import org.nrg.xnat.importer.XnatImportRequest
import org.nrg.xnat.importer.importers.DefaultImporterRequest
import org.nrg.xnat.importer.importers.SessionImporterRequest
import org.nrg.xnat.importer.params.FileRequest
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.rest.SerializationUtils

class ImporterSubinterface extends CoreXnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/services/archive',
                '/services/import'
        ]
    }

    String callImporter(XnatImportRequest importRequest) throws ImportException {
        final RequestSpecification baseReq = (importRequest instanceof FileRequest && importRequest.file != null) ?
                queryBase().multiPart(importRequest.file) :
                queryBase().multiPart(new MultiPartSpecBuilder("").build())
        final Response response = baseReq.queryParams(SerializationUtils.serializeToMap(importRequest)).post(formatRestUrl('/services/import'))
        if (response.statusCode() == 200) {
            response.asString().trim()
        } else {
            throw new ImportException(response.statusCode(), response.asString())
        }
    }

    XnatInterface uploadToSessionZipImporter(File sessionZip, Project project, Subject subject = null, ImagingSession session = null) throws ImportException {
        if (project == null) {
            throw new IllegalArgumentException('Project cannot be null when uploading to zip importer.')
        }

        final SessionImporterRequest importerRequest = new DefaultImporterRequest().file(sessionZip).destArchive().project(project)
        if (subject != null) {
            importerRequest.subject(subject)
        }
        if (session != null) {
            importerRequest.exptLabel(session)
        }
        callImporter(importerRequest)

        if (session != null) {
            xnatInterface.getAccessionNumber(project, session)
        }
        xnatInterface
    }

    XnatInterface uploadToSessionZipImporter(File sessionZip, ImagingSession session) throws ImportException {
        if (session.primaryProject == null) throw new IllegalArgumentException('Session must have project object specified to use this shortcut method')
        uploadToSessionZipImporter(sessionZip, session.primaryProject, session.subject, session)
    }

    XnatInterface requestArchival(XnatArchivalRequest archivalRequest) {
        queryBase().queryParams(SerializationUtils.serializeToMap(archivalRequest)).
                post(formatRestUrl('services/archive')).then().assertThat().statusCode(200)
        xnatInterface
    }

}
