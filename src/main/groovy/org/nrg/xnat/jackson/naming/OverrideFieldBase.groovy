package org.nrg.xnat.jackson.naming

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)

@interface OverrideFieldBase {
    String value()
}
