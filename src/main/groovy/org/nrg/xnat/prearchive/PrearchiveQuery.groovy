package org.nrg.xnat.prearchive

class PrearchiveQuery {

    PrearchiveQueryScope scope = PrearchiveQueryScope.FULL_PREARCHIVE
    PrearchiveResultFilter filter
    PrearchiveResultExpectations expectedResult = PrearchiveResultExpectations.ANYTHING
    int maxQueryTimeSeconds = 600

    PrearchiveQuery scope(PrearchiveQueryScope scope) {
        this.scope = scope
        this
    }

    PrearchiveQuery filter(PrearchiveResultFilter filter) {
        this.filter = filter
        this
    }

    PrearchiveQuery expectedResult(PrearchiveResultExpectations expectedResult) {
        this.expectedResult = expectedResult
        this
    }

    PrearchiveQuery maxQueryTime(int seconds) {
        maxQueryTimeSeconds = seconds
        this
    }

}
