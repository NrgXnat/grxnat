package org.nrg.xnat.pogo.resources

import org.apache.commons.lang3.tuple.Pair

class ResourceMitigationReport {

    long resourceSurveyRequestId
    Map<File, File> movedFiles
    Map<File, File> removedFiles
    Map<File, Pair<File, String>> backupErrors
    Map<File, Pair<File, String>> moveErrors
    Map<File, String> deleteErrors
    String catalogWriteError
    String resourceSaveError
    int totalMovedFiles
    int totalRemovedFiles
    int totalFileErrors
    
}
