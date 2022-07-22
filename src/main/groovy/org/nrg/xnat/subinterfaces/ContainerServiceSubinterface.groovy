package org.nrg.xnat.subinterfaces

import io.restassured.response.ValidatableResponse
import org.hamcrest.Matchers
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.meta.RequireAdmin
import org.nrg.xnat.meta.RequirePlugin
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.PluginRegistry
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.containers.Command
import org.nrg.xnat.pogo.containers.CommandSummaryForContext
import org.nrg.xnat.pogo.containers.DockerServer
import org.nrg.xnat.pogo.containers.Image
import org.nrg.xnat.pogo.containers.Orchestration
import org.nrg.xnat.pogo.containers.OrchestrationProject
import org.nrg.xnat.pogo.containers.Wrapper
import org.nrg.xnat.rest.SerializationUtils

import static io.restassured.http.ContentType.JSON

@RequirePlugin(PluginRegistry.CS_PLUGIN_ID)
class ContainerServiceSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/commands',
                '/xapi/commands/available',
                '/xapi/commands/available/site',
                '/xapi/docker/image-summaries',
                '/xapi/docker/images',
                '/xapi/docker/images/save',
                '/xapi/docker/pull',
                '/xapi/docker/server',
                '/xapi/wrappers/{wrapperId}/enabled',
                '/xapi/wrappers/{wrapperId}/disabled',
                '/xapi/projects/{project}/wrappers/{wrapperId}/disabled',
                '/xapi/projects/{project}/wrappers/{wrapperId}/enabled',
                '/xapi/projects/{project}/wrappers/{wrapperId}/root/{rootElement}/bulklaunch',
                '/xapi/projects/{project}/wrappers/{wrapperId}/root/{rootElement}/launch',
                '/xapi/orchestration',
                '/xapi/orchestration/{id}',
                '/xapi/orchestration/project/{project}',
                '/xapi/orchestration/{id}/enabled/{enabled}'
        ]
    }

    List<Image> readImages(String imageFilter = null) {
        SerializationUtils.deserializeList(queryBase().get(formatXapiUrl('docker', 'image-summaries')).
                then().assertThat().statusCode(200).and().extract().
                jsonPath().getList(imageFilter ?: ''), Image)
    }

    @RequireAdmin
    XnatInterface deleteImage(String imageId, boolean force = false) {
        queryBase().queryParam('force', force).delete(formatXapiUrl('docker', 'images', imageId)).then().assertThat().statusCode(204)
        xnatInterface
    }

    @RequireAdmin
    XnatInterface deleteImage(Image image, boolean force = false) {
        if (image.imageId == null) {
            image.setImageId(
                    readImages().find { possibleMatch ->
                        possibleMatch == image
                    }.imageId
            )
        }
        deleteImage(image.imageId, force)
    }

    List<Command> readCommands(String fullImageTag = null) {
        final Map<String, String> queryParams = fullImageTag != null ? ['image' : fullImageTag] : [:]
        SerializationUtils.deserializeList(
                queryBase().queryParams(queryParams).get(formatXapiUrl('commands')).then().assertThat().statusCode(200).and().extract().jsonPath().getList('$'),
                Command
        )
    }

    List<Command> readCommands(Image image) {
        readCommands(image.toString())
    }

    @RequireAdmin
    XnatInterface pullImage(String fullImageTag, boolean saveCommands = true) {
        queryBase().queryParam('save-commands', saveCommands).queryParam('image', fullImageTag).post(formatXapiUrl('/docker/pull')).then().assertThat().statusCode(200)
        xnatInterface
    }

    @RequireAdmin
    XnatInterface pullImage(Image image, boolean saveCommands = true) {
        pullImage(image.toString(), saveCommands)
    }

    @RequireAdmin
    XnatInterface saveCommandsFromLabels(String fullImageTag) {
        queryBase().queryParam('image', fullImageTag).post(formatXapiUrl('/docker/images/save')).then().assertThat().statusCode(200)
        xnatInterface
    }

    @RequireAdmin
    XnatInterface saveCommandsFromLabels(Image image) {
        saveCommandsFromLabels(image.toString())
    }

    @RequireAdmin
    int addCommand(File commandJson) {
        queryBase().contentType(JSON).body(commandJson).post(formatXapiUrl('/commands')).then().assertThat().statusCode(201).extract().as(Integer)
    }

    DockerServer readDockerServer() {
        queryBase().get(formatXapiUrl('/docker/server')).then().assertThat().statusCode(200).extract().as(DockerServer)
    }

    @RequireAdmin
    XnatInterface updateDockerServer(DockerServer dockerServerSpec) {
        dockerServerSpec.setId(null)
        if (dockerServerSpec.swarmConstraints != null) {
            dockerServerSpec.swarmConstraints.each { constraint ->
                constraint.setId(null)
            }
        }
        queryBase().body(dockerServerSpec).contentType(JSON).post(formatXapiUrl('/docker/server')).then().assertThat().statusCode(201)
        xnatInterface
    }

    List<CommandSummaryForContext> readAvailableCommands(DataType dataType, Project project = null) {
        final Map<String, String> queryParams = ['xsiType' : dataType.xsiType]
        final List<String> urlComponents = ['commands', 'available']
        if (project == null) {
            urlComponents << 'site'
        } else {
            queryParams.put('project', project.id)
        }

        SerializationUtils.deserializeList(
                queryBaseWithStatusCodeListeners([FORBIDDEN_403]).queryParams(queryParams).get(formatXapiUrl(urlComponents.join('/'))).then().assertThat().statusCode(200).and().extract().jsonPath().getList('$'),
                CommandSummaryForContext
        )
    }

    XnatInterface setWrapperStatusOnProject(long wrapperId, Project project, boolean enable) {
        queryBase().put(formatXapiUrl('/projects', project.id, '/wrappers/', String.valueOf(wrapperId), enable ? 'enabled' : 'disabled')).then().assertThat().statusCode(200)
        xnatInterface
    }

    XnatInterface setWrapperStatusOnProject(Wrapper wrapper, Project project, boolean enable) {
        setWrapperStatusOnProject(wrapper.id, project, enable)
    }

    XnatInterface setWrapperStatusOnProject(CommandSummaryForContext availableCommand, Project project, boolean enable) {
        setWrapperStatusOnProject(availableCommand.wrapperId, project, enable)
    }

    @RequireAdmin
    XnatInterface setWrapperStatusOnSite(long wrapperId, boolean enable) {
        queryBase().put(formatXapiUrl('/wrappers/', String.valueOf(wrapperId), enable ? 'enabled' : 'disabled')).then().assertThat().statusCode(200)
        xnatInterface
    }

    @RequireAdmin
    XnatInterface setWrapperStatusOnSite(Wrapper wrapper, boolean enable) {
        setWrapperStatusOnSite(wrapper.id, enable)
    }

    @RequireAdmin
    XnatInterface setWrapperStatusOnSite(CommandSummaryForContext availableCommand, Project project, boolean enable) {
        setWrapperStatusOnSite(availableCommand.wrapperId, enable)
    }

    int launchContainer(Project project, long wrapperId, String rootElementName, String rootElementUri, Map<String, String> otherInputs = [:]) {
        final Map<String, String> params = otherInputs.clone() as Map<String, String>
        params.put(rootElementName, rootElementUri)
        issueContainerLaunchRequest(project, wrapperId, rootElementName, false, params).body('status', Matchers.equalTo('success')).extract().jsonPath().getInt('workflow-id')
    }

    int launchContainer(Project project, Wrapper wrapper, String rootElementUri, Map<String, String> otherInputs = [:]) {
        launchContainer(project, wrapper.id, wrapper.externalInputs[0].name, rootElementUri, otherInputs)
    }

    int launchContainer(Project project, CommandSummaryForContext wrapper, String rootElementUri, Map<String, String> otherInputs = [:]) {
        launchContainer(project, wrapper.wrapperId, wrapper.rootElementName, rootElementUri, otherInputs)
    }

    XnatInterface bulkLaunchContainers(Project project, long wrapperId, String rootElementName, Collection<String> rootElementUris, Map<String, String> otherInputs = [:]) {
        final Map<String, String> params = otherInputs.clone() as Map<String, String>
        params.put(rootElementName, '["' + rootElementUris.join('","') + '"]')
        issueContainerLaunchRequest(project, wrapperId, rootElementName, true, params).assertThat().
                body('successes', Matchers.hasSize(rootElementUris.size())).
                body('failures', Matchers.hasSize(0))
        xnatInterface
    }

    XnatInterface bulkLaunchContainers(Project project, Wrapper wrapper, Collection<String> rootElementUris, Map<String, String> otherInputs = [:]) {
        bulkLaunchContainers(project, wrapper.id, wrapper.externalInputs[0].name, rootElementUris, otherInputs)
    }

    XnatInterface bulkLaunchContainers(Project project, CommandSummaryForContext wrapper, Collection<String> rootElementUris, Map<String, String> otherInputs = [:]) {
        bulkLaunchContainers(project, wrapper.wrapperId, wrapper.rootElementName, rootElementUris, otherInputs)
    }

    @RequireAdmin
    Orchestration createOrUpdateOrchestration(Orchestration orchestration) {
        queryBase().body(orchestration).contentType(JSON)
                .post(formatXapiUrl('/orchestration'))
                .then().assertThat().statusCode(200).extract().as(Orchestration)
    }

    OrchestrationProject getProjectOrchestrationConfig(Project project) {
        queryBase().contentType(JSON)
                .get(formatXapiUrl('/orchestration', 'project', project.id))
                .then().assertThat().statusCode(200).extract().as(OrchestrationProject)
    }

    XnatInterface setProjectOrchestration(Project project, Orchestration orchestration) {
        queryBase().queryParam("orchestrationId", orchestration.getId())
                .put(formatXapiUrl('/orchestration', 'project', project.id))
                .then().assertThat().statusCode(200)
        xnatInterface
    }

    XnatInterface removeProjectOrchestration(Project project) {
        queryBase().delete(formatXapiUrl('/orchestration', 'project', project.id))
                .then().assertThat().statusCode(200)
        xnatInterface
    }

    @RequireAdmin
    XnatInterface deleteOrchestration(Orchestration orchestration) {
        queryBase().delete(formatXapiUrl('/orchestration', String.valueOf(orchestration.id)))
                .then().assertThat().statusCode(200)
        xnatInterface
    }

    @RequireAdmin
    XnatInterface enableOrDisableOrchestration(Orchestration orchestration, boolean enabled) {
        queryBase().put(formatXapiUrl('/orchestration', String.valueOf(orchestration.id), 'enabled',String.valueOf(enabled)))
                .then().assertThat().statusCode(200)
        xnatInterface
    }

    protected ValidatableResponse issueContainerLaunchRequest(Project project, long wrapperId, String rootElementName, boolean isBulk, Map<String, String> allQueryParams) {
        queryBase().body(allQueryParams).contentType(JSON).post(formatXapiUrl('/projects', project.id, 'wrappers', String.valueOf(wrapperId), 'root', rootElementName, isBulk ? 'bulklaunch' : 'launch')).
                then().assertThat().statusCode(200).and()
    }

}
