package org.nrg.xnat.pogo.containers

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Orchestration {
    long id = 0L
    String name
    List<Long> wrapperIds
    boolean enabled = true

    Orchestration() {}
    Orchestration(String name, List<Long> wrapperIds) {
        this.name = name
        this.wrapperIds = wrapperIds
    }
}
