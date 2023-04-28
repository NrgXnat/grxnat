package org.nrg.xnat.pogo.search

import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@TupleConstructor
@Builder(builderStrategy = SimpleStrategy, prefix = '')
class XnatSearchDocument {

    String searchXml

}
