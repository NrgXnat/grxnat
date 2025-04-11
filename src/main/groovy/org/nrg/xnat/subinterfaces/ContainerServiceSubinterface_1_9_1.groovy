package org.nrg.xnat.subinterfaces

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.meta.RequireAdmin
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.containers.Command
import org.nrg.xnat.pogo.containers.CommandSummaryForContext
import org.nrg.xnat.pogo.containers.DockerServer
import org.nrg.xnat.pogo.containers.Image
import org.nrg.xnat.pogo.containers.Orchestration
import org.nrg.xnat.pogo.containers.Wrapper
import org.nrg.xnat.versions.XnatVersion
import org.nrg.xnat.versions.XnatVersionList
import org.nrg.xnat.versions.Xnat_1_9_2

class ContainerServiceSubinterface_1_9_1 extends ContainerServiceSubinterface {

    @Override
    List<Class<? extends XnatVersion>> supportedVersions() {
        XnatVersionList.knownVersionsBefore(Xnat_1_9_2)
    }

    @Override
    @RequireAdmin
    XnatInterface deleteImage(String imageId, boolean force = false) {
        super.deleteImage(imageId, force)
    }

    @Override
    @RequireAdmin
    XnatInterface deleteImage(Image image, boolean force = false) {
        super.deleteImage(image, force)
    }

    @Override
    @RequireAdmin
    XnatInterface deleteCommand(Command command) {
        super.deleteCommand(command.id)
    }

    @Override
    @RequireAdmin
    XnatInterface deleteCommand(int commandId) {
        super.deleteCommand(commandId)
    }

    @Override
    @RequireAdmin
    XnatInterface deleteAllCommands() {
        super.deleteAllCommands()
    }

    @Override
    @RequireAdmin
    XnatInterface pullImage(String fullImageTag, boolean saveCommands = true) {
        super.pullImage(fullImageTag, saveCommands)
    }

    @Override
    @RequireAdmin
    XnatInterface pullImage(Image image, boolean saveCommands = true) {
        super.pullImage(image, saveCommands)
    }

    @Override
    @RequireAdmin
    XnatInterface saveCommandsFromLabels(String fullImageTag) {
        super.saveCommandsFromLabels(fullImageTag)
    }

    @Override
    @RequireAdmin
    XnatInterface saveCommandsFromLabels(Image image) {
        super.saveCommandsFromLabels(image)
    }

    @Override
    @RequireAdmin
    int addCommand(File commandJson) {
        super.addCommand(commandJson)
    }

    @Override
    @RequireAdmin
    int addCommand(String commandJson) {
        super.addCommand(commandJson)
    }

    @Override
    @RequireAdmin
    XnatInterface updateDockerServer(DockerServer dockerServerSpec) {
        super.updateDockerServer(dockerServerSpec)
    }

    @Override
    @RequireAdmin
    XnatInterface setWrapperStatusOnSite(long wrapperId, boolean enable) {
        super.setWrapperStatusOnSite(wrapperId, enable)
    }

    @Override
    @RequireAdmin
    XnatInterface setWrapperStatusOnSite(Wrapper wrapper, boolean enable) {
        super.setWrapperStatusOnSite(wrapper, enable)
    }

    @Override
    @RequireAdmin
    XnatInterface setWrapperStatusOnSite(CommandSummaryForContext availableCommand, Project project, boolean enable) {
        super.setWrapperStatusOnSite(availableCommand, project, enable)
    }

    @Override
    @RequireAdmin
    Orchestration createOrUpdateOrchestration(Orchestration orchestration) {
        super.createOrUpdateOrchestration(orchestration)
    }

    @Override
    @RequireAdmin
    XnatInterface deleteOrchestration(Orchestration orchestration) {
        super.deleteOrchestration(orchestration)
    }

    @Override
    @RequireAdmin
    XnatInterface enableOrDisableOrchestration(Orchestration orchestration, boolean enabled) {
        super.enableOrDisableOrchestration(orchestration, enabled)
    }

}

