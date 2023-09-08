package org.nrg.xnat.pogo.dqr

class DqrSearchResponse<X extends DqrObject> {

    boolean hasLimitedResultSetSize
    StudyDateRangeLimitResults studyDateRangeLimitResults
    List<X> results

}
