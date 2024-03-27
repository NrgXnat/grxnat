package org.nrg.xnat.pogo.dqr

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@ToString
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(excludes = ['ormStrategySpringBeanId'])
class PacsConnection {

    Integer id
    String aeTitle
    String host
    String label
    Integer queryRetrievePort
    boolean dicomWebEnabled
    String dicomWebRootUrl
    boolean queryable
    boolean defaultQrAe
    boolean storable
    boolean defaultStorageAe
    String dicomObjectIdentifier
    String ormStrategySpringBeanId = 'dicomOrmStrategy'

}
