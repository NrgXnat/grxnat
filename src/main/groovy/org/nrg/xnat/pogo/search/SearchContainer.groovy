package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

abstract class SearchContainer {

    @JacksonXmlProperty(localName = 'xdat:child_set')
    @JacksonXmlElementWrapper(useWrapping = false)
    List<XdatChildSet> childSets

    @JacksonXmlProperty(localName = 'xdat:criteria')
    @JacksonXmlElementWrapper(useWrapping = false)
    List<XdatCriteria> criteria

    void addSearchCriterion(SearchCriterion criterion) {
        if (criterion instanceof XdatChildSet) {
            childSets = childSets ?: []
            childSets << criterion
        } else if (criterion instanceof XdatCriteria) {
            criteria = criteria ?: []
            criteria << criterion
        } else {
            throw new UnsupportedOperationException('Unknown criteria type')
        }
    }

    List<XdatCriteria> findRecursiveCriteria() {
        final List<XdatCriteria> accumulated = []
        if (criteria != null) {
            accumulated.addAll(criteria)
        }
        if (childSets != null) {
            childSets.each { childSet ->
                accumulated.addAll(childSet.findRecursiveCriteria())
            }
        }
        accumulated
    }

}
