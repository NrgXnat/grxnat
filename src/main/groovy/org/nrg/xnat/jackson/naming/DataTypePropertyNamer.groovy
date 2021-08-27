package org.nrg.xnat.jackson.naming

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.cfg.MapperConfig
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod

class DataTypePropertyNamer extends PropertyNamingStrategy {

    String prefix
    PropertyNamingStrategy baseNamer

    DataTypePropertyNamer(String prefix, PropertyNamingStrategy baseNamer) {
        this.prefix = prefix
        this.baseNamer = baseNamer
    }

    @Override
    String nameForGetterMethod(MapperConfig config, AnnotatedMethod method, String defaultName) {
        prefix + '/' + (readOverridingBase(method) ?: baseNamer.nameForGetterMethod(config, method, defaultName))
    }

    @Override
    String nameForSetterMethod(MapperConfig config, AnnotatedMethod method, String defaultName) {
        readOverridingBase(method) ?: baseNamer.nameForSetterMethod(config, method, defaultName)
    }

    protected String readOverridingBase(AnnotatedMethod method) {
        method.getAnnotation(OverrideFieldBase)?.value()
    }

}
