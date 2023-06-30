package org.nrg.xnat.pogo.resources

class ResourceSurveyReport {

    long resourceSurveyRequestId
    Date surveyDate
    int totalEntries
    int totalUids
    int totalBadFiles
    int totalMismatchedFiles
    int totalDuplicates
    int totalFilesInDuplicates
    int totalNonActionableDuplicates
    int totalFilesInNonActionableDuplicates
    Map<String, List<String>> uids
    List<File> badFiles
    Map<File, String> mismatchedFiles
    Map<String, Map<String, Map<File, String>>> duplicates
    Map<String, Map<String, Map<File, String>>> nonActionableDuplicates

}
