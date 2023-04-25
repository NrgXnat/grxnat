package org.nrg.xnat.pogo.dqr

import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(excludes = ['ormStrategySpringBeanId'])
class PacsConnection {

    String aeTitle
    String host
    String label
    Integer queryRetrievePort
    boolean queryable
    boolean defaultQrAe
    boolean storable
    boolean defaultStorageAe
    boolean dicomWebEnabled
    String dicomWebUrl
    String ormStrategySpringBeanId = 'dicomOrmStrategy'
    Integer id

}
