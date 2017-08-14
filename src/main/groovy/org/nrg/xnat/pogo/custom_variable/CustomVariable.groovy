package org.nrg.xnat.pogo.custom_variable

import org.nrg.xnat.util.ListUtils

class CustomVariable<T> {

    String name
    boolean required = false
    private final List<T> possibleValues = []

    public CustomVariable(String name) {
        this.name = name
    }

    public CustomVariable() {}

    public List<T> getPossibleValues() {
        return possibleValues
    }

    public void setPossibleValues(List<T> possibleValues) {
        ListUtils.copyInto(possibleValues, this.possibleValues)
    }

    public CustomVariable name(String name) {
        setName(name)
        return this
    }

    public CustomVariable required(boolean required) {
        setRequired(required)
        return this
    }

    public CustomVariable possibleValues(List<T> possibleValues) {
        ListUtils.copyInto(possibleValues, this.possibleValues)
        return this
    }

}
