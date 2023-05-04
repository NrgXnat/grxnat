package org.nrg.xnat.pogo.search

import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@TupleConstructor
@Builder(builderStrategy = SimpleStrategy, prefix = '')
class XnatSearchDocument {

    public static final String XML_NAMESPACES_TO_RETAIN = ['arc', 'val', 'pipe', 'wrk', 'scr', 'xdat', 'cat', 'prov', 'xnat', 'xnat_a', 'xsi'].join('|')
    public static final String NAMESPACE_PATTERN = " (xmlns:(?!${XML_NAMESPACES_TO_RETAIN})\\S+?=\"\\S+?\")|(xsi:schemaLocation=\".*?\") ?"
    String searchXml

    XnatSearchDocument genericize() {
        searchXml = genericizedXml()
        this
    }

    // to strip out things added by plugins
    String genericizedXml() {
        searchXml.replaceAll(NAMESPACE_PATTERN, '')
    }

}
