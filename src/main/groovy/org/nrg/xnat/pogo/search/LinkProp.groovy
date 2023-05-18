package org.nrg.xnat.pogo.search

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class LinkProp {

    String name
    String value
    List<LinkPropInsert> inserts

}
