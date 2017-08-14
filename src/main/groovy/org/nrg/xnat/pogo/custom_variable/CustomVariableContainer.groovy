package org.nrg.xnat.pogo.custom_variable

import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.Extensible
import org.nrg.xnat.util.MapUtils

abstract class CustomVariableContainer<X extends Extensible<X>> extends Extensible<X> {

    private final Map<CustomVariableSet, Map<CustomVariable, Object>> customVariables = [:]

    CustomVariableContainer(Extension<X> extension) {
        super(extension)
    }

    CustomVariableContainer() {
        super(null)
    }

    Map<CustomVariableSet, Map<CustomVariable, Object>> getCustomVariables() {
        return customVariables
    }

    void setCustomVariables(Map<CustomVariableSet, Map<CustomVariable, Object>> customVariables) {
        MapUtils.copyInto(customVariables, this.customVariables)
    }

    void addCustomVariableSet(CustomVariableSet variableSet, Map<CustomVariable, Object> variables) {
        customVariables.put(variableSet, variables)
    }

}
