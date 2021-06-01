package org.nrg.xnat.pogo.events

import org.nrg.xnat.enums.PaginatedApiFilterOperator

class DeliveredEventQueryFilter {

    PaginatedApiFilterOperator operator
    String value
    String backend = 'hibernate'

}
