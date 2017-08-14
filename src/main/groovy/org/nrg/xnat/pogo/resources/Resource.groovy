package org.nrg.xnat.pogo.resources

import org.nrg.xnat.pogo.Extensible
import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.pogo.experiments.SubjectAssessor
import org.nrg.xnat.util.ListUtils

abstract class Resource extends Extensible<Resource> {
    Project project
    Subject subject
    SubjectAssessor subjectAssessor
    Scan scan
    SessionAssessor sessionAssessor
    String folder
    String format
    protected final List<ResourceFile> resourceFiles = []
    protected Extension<Resource> extension

    Resource() {}

    abstract String resourceUrl()

    List<ResourceFile> getResourceFiles() {
        return resourceFiles
    }

    void setResourceFiles(List<ResourceFile> resourceFiles) {
        ListUtils.copyInto(resourceFiles, this.resourceFiles)
    }

    Extension<Resource> getExtension() {
        return extension
    }

    void setExtension(Extension<Resource> extension) {
        this.extension = extension
    }

    Resource project(Project project) {
        setProject(project)
        return this
    }

    Resource subject(Subject subject) {
        setSubject(subject)
        return this
    }

    Resource subjectAssessor(SubjectAssessor subjectAssessor) {
        setSubjectAssessor(subjectAssessor)
        return this
    }

    Resource scan(Scan scan) {
        setScan(scan)
        return this
    }

    Resource sessionAssessor(SessionAssessor sessionAssessor) {
        setSessionAssessor(sessionAssessor)
        return this
    }

    Resource folder(String label) {
        setFolder(label)
        return this
    }

    Resource format(String format) {
        setFormat(format)
        return this
    }

    Resource resourceFiles(List<ResourceFile> resourceFiles) {
        setResourceFiles(resourceFiles)
        return this
    }

    Resource addResourceFile(ResourceFile resourceFile) {
        resourceFiles.add(resourceFile)
        return this
    }

    Resource extension(Extension<Resource> extension) {
        setExtension(extension)
        return this
    }

}
