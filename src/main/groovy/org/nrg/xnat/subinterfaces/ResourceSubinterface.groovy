package org.nrg.xnat.subinterfaces

import org.nrg.xnat.pogo.extensions.SimpleResourceFileExtension
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.pogo.resources.ResourceFile
import org.nrg.xnat.rest.SerializationUtils

import static org.nrg.testing.CommonStringUtils.formatUrl

class ResourceSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/projects/{PROJECT_ID}/resources',
                '/projects/{PROJECT_ID}/resources/{RESOURCE_ID}',
                '/projects/{PROJECT_ID}/resources/{RESOURCE_ID}/files',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/resources',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/resources/{RESOURCE_ID}',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/resources/{RESOURCE_ID}/files',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}/resources',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}/resources/{RESOURCE_ID}',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}/resources/{RESOURCE_ID}/files',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{ASSESSED_ID}/assessors/{EXPT_ID}/resources',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{ASSESSED_ID}/assessors/{EXPT_ID}/resources/{RESOURCE_ID}',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{ASSESSED_ID}/assessors/{EXPT_ID}/resources/{RESOURCE_ID}/files',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{ASSESSED_ID}/reconstructions/{RECON_ID}/resources',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{ASSESSED_ID}/reconstructions/{RECON_ID}/resources/{RESOURCE_ID}',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{ASSESSED_ID}/reconstructions/{RECON_ID}/resources/{RESOURCE_ID}/files',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{ASSESSED_ID}/scans/{SCAN_ID}/resources',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{ASSESSED_ID}/scans/{SCAN_ID}/resources/{RESOURCE_ID}',
                '/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{ASSESSED_ID}/scans/{SCAN_ID}/resources/{RESOURCE_ID}/files'
        ]
    }

    String resourceFilesUrl(Resource resource) {
        formatXnatUrl("${resource.resourceUrl()}/resources/${resource.folder}/files")
    }

    String resourceFileUrl(Resource resource, ResourceFile file) {
        formatUrl(resourceFilesUrl(resource), file.name)
    }

    Resource findResource(List<Resource> resources, String resourceLabel) {
        resources.find { it.folder == resourceLabel }
    }

    void uploadResources(List<Resource> resources) {
        resources.each { resource ->
            uploadResource(resource)
        }
    }

    void uploadResource(Resource resource) {
        if (resource.folder == null) {
            throw new UnsupportedOperationException('Resource is missing a label (folder).')
        }

        queryBase().queryParams(SerializationUtils.serializeToMap(resource)).put(formatXnatUrl("${resource.resourceUrl()}/resources/${resource.folder}")).then().assertThat().statusCode(200)

        resource.resourceFiles.each { file ->
            if (file.extension == null) {
                final File possibleFile = new File(file.getName())
                if (possibleFile != null && possibleFile.exists() && possibleFile.isFile()) {
                    file.extension(new SimpleResourceFileExtension(file, possibleFile))
                } else {
                    throw new UnsupportedOperationException('ResourceFile must have extension set in order to locate file for upload.')
                }
            }
            file.extension.uploadTo(xnatInterface, resource)
        }

        final Resource responseResource = jsonQuery().get(formatXnatUrl(resource.resourceUrl(), 'resources')).then().assertThat().statusCode(200).
                and().extract().response().jsonPath().getObject("ResultSet.Result.find { it.label == '${resource.folder}' }", resource.class as Class<Resource>)

        resource.fileCount(responseResource.fileCount).fileSize(responseResource.fileSize)
    }

    /**
     * Reads the list of Resource objects at the XNAT-level specified in the dummyResource
     * @param dummyResource
     * @return
     */
    List<Resource> readResources(Resource dummyResource) {
        final List resourceResp = jsonQuery().get(formatXnatUrl(dummyResource.resourceUrl(), 'resources')).then().assertThat().statusCode(200).
                and().extract().response().jsonPath().getList('ResultSet.Result')
        final List<Resource> resources = SerializationUtils.deserializeList(resourceResp, dummyResource.class)
        resources.each { resource ->
            if (dummyResource.project != null) resource.project(dummyResource.project)
            if (dummyResource.subject != null) resource.subject(dummyResource.subject)
            if (dummyResource.subjectAssessor != null) resource.subjectAssessor(dummyResource.subjectAssessor)
            if (dummyResource.scan != null) resource.scan(dummyResource.scan)
            if (dummyResource.sessionAssessor != null) resource.sessionAssessor(dummyResource.sessionAssessor)
            readResourceFiles(resource)
        }
        resources
    }

    List<ResourceFile> readResourceFiles(Resource resource) {
        resource.resourceFiles(
                jsonQuery().get(resourceFilesUrl(resource)).jsonPath().getObject('ResultSet.Result', ResourceFile[]) as List<ResourceFile>
        ).resourceFiles
    }

    InputStream streamResourceFile(Resource resource, ResourceFile resourceFile) {
        queryBase().get(resourceFileUrl(resource, resourceFile)).then().assertThat().statusCode(200).extract().asInputStream()
    }

    void deleteResource(Resource resource) {
        queryBase().queryParam('removeFiles', true).delete(formatXnatUrl(resource.resourceUrl(), "resources/${resource.folder}")).then().assertThat().statusCode(200)
    }

}
