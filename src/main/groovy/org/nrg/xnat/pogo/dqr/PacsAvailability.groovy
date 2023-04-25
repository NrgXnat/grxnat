package org.nrg.xnat.pogo.dqr

import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import java.time.DayOfWeek

@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor
class PacsAvailability {

    String availabilityEnd
    String availabilityStart
    DayOfWeek dayOfWeek
    Integer pacsId
    Integer threads
    Integer utilizationPercent
    Integer id

    static PacsAvailability withDefaultOptions(DayOfWeek day, Integer pacsId) {
        new PacsAvailability('24:00', '00:00', day, pacsId, 1, 100)
    }

}
