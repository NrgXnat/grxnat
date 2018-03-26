package org.nrg.xnat.pogo.custom_variable

import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.Extensible
import org.nrg.xnat.util.MapUtils

abstract class CustomVariableContainer<X extends Extensible<X>> extends Extensible<X> {

    final Map<String, Object> fields = [:]

    CustomVariableContainer(Extension<X> extension) {
        super(extension)
    }

    CustomVariableContainer() {
        super(null)
    }

    void setFields(Map<String, Object> customVariables) {
        MapUtils.copyInto(customVariables, this.fields)
    }

    @SuppressWarnings("unchecked")
    <T extends CustomVariableContainer> T fields(Map<String, Object> customVariables) {
        setFields(customVariables)
        this as T
    }

    abstract String fieldBaseDataType()

}
