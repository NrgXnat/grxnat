package org.nrg.xnat.prearchive

import org.nrg.xnat.enums.PrearchiveStatus

import java.util.function.Predicate

abstract class PrearchiveResultExpectations {

    public static final PrearchiveResultExpectations EMPTY = ofSize(0)
    public static final PrearchiveResultExpectations SINGLE_RESULT = ofSize(1)
    public static final PrearchiveResultExpectations ANYTHING = new PrearchiveResultExpectations() {
        @Override
        Predicate<List<SessionData>> getMatcher() {
            (data) -> true
        }
    }

    static PrearchiveResultExpectations ofSize(int expectedSize) {
        new PrearchiveResultExpectations() {
            @Override
            Predicate<List<SessionData>> getMatcher() {
                (List<SessionData> data) -> data.size() == expectedSize
            }
        }
    }

    static PrearchiveResultExpectations allResultsHaveStatus(PrearchiveStatus status) {
        new PrearchiveResultExpectations() {
            @Override
            Predicate<List<SessionData>> getMatcher() {
                (List<SessionData> data) -> {
                    data.every { session ->
                        session.status == status
                    }
                }
            }
        }
    }

    abstract Predicate<List<SessionData>> getMatcher()

}
