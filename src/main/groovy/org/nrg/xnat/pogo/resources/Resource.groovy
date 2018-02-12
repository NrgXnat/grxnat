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
    int fileCount
    long fileSize
    protected final List<ResourceFile> resourceFiles = []
    protected Extension<Resource> extension

    Resource() {}

    abstract String resourceUrl()

    List<ResourceFile> getResourceFiles() {
        resourceFiles
    }

    void setResourceFiles(List<ResourceFile> resourceFiles) {
        ListUtils.copyInto(resourceFiles, this.resourceFiles)
    }

    Extension<Resource> getExtension() {
        extension
    }

    void setExtension(Extension<Resource> extension) {
        this.extension = extension
    }

    Resource project(Project project) {
        setProject(project)
        this
    }

    Resource subject(Subject subject) {
        setSubject(subject)
        this
    }

    Resource subjectAssessor(SubjectAssessor subjectAssessor) {
        setSubjectAssessor(subjectAssessor)
        this
    }

    Resource scan(Scan scan) {
        setScan(scan)
        this
    }

    Resource sessionAssessor(SessionAssessor sessionAssessor) {
        setSessionAssessor(sessionAssessor)
        this
    }

    Resource folder(String label) {
        setFolder(label)
        this
    }

    Resource format(String format) {
        setFormat(format)
        this
    }

    Resource fileCount(int count) {
        setFileCount(count)
        this
    }

    Resource fileSize(long size) {
        setFileSize(size)
        this
    }

    Resource resourceFiles(List<ResourceFile> resourceFiles) {
        setResourceFiles(resourceFiles)
        this
    }

    Resource addResourceFile(ResourceFile resourceFile) {
        if (!resourceFiles.contains(resourceFile)) resourceFiles.add(resourceFile)
        this
    }

    Resource extension(Extension<Resource> extension) {
        setExtension(extension)
        this
    }

    ResourceFile findFile(String fullPath) {
        resourceFiles.find { it.fullPath() == fullPath }
    }

    ResourceFile findFileMatch(String fullPathRegex) {
        resourceFiles.find { it.fullPath().matches(fullPathRegex) }
    }

    @Override
    String toString() {
        folder
    }

}
