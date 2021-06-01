package org.nrg.xnat.pogo.events

import org.nrg.xnat.enums.PaginatedApiFilterOperator
import org.nrg.xnat.enums.PaginatedApiSortDirection

class DeliveredEventQueryBuilder {

    int page = 1
    Integer size
    DeliveredEventQuerySortColumn sortCol
    PaginatedApiSortDirection sortDir
    Map<DeliveredEventQueryFilterKey, DeliveredEventQueryFilter> filters = [:]

    DeliveredEventQueryBuilder page(int page) {
        this.page = page
        this
    }

    DeliveredEventQueryBuilder size(int size) {
        this.size = size
        this
    }

    DeliveredEventQueryBuilder sort(DeliveredEventQuerySortColumn sortCol, PaginatedApiSortDirection sortDir) {
        this.sortCol = sortCol
        this.sortDir = sortDir
        this
    }

    DeliveredEventQueryBuilder filter(DeliveredEventQueryFilterKey filterKey, String value, PaginatedApiFilterOperator operator = PaginatedApiFilterOperator.LIKE) {
        filters.put(filterKey, new DeliveredEventQueryFilter(operator: operator, value: value))
        this
    }

    DeliveredEventQueryBuilder filter(Subscription subscription) {
        filter(DeliveredEventQueryFilterKey.SUBSCRIPTION, subscription.name, PaginatedApiFilterOperator.LIKE)
    }

    DeliveredEventQueryRequest build() {
        new DeliveredEventQueryRequest(
                page: page,
                size: size,
                sortCol: sortCol,
                sortDir: sortDir,
                filters: filters
        )
    }

}
