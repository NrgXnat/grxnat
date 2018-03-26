package org.nrg.xnat.pogo.custom_variable

import org.nrg.xnat.util.ListUtils

import java.time.LocalDate

class CustomVariable<T> {

    String name
    boolean required = false
    String type
    private final List<T> possibleValues = []

    CustomVariable(String name) {
        this.name = name
    }

    CustomVariable(Class<T> type) {
        switch (type) {
            case String:
                dataType = 'string'
                break
            case Integer:
                dataType = 'integer'
                break
            case Double:
            case Float:
                dataType = 'float'
                break
            case Boolean:
                dataType = 'boolean'
                break
            case LocalDate:
                dataType = 'date'
                break
        }
    }

    CustomVariable() {}

    List<T> getPossibleValues() {
        return possibleValues
    }

    void setPossibleValues(List<T> possibleValues) {
        ListUtils.copyInto(possibleValues, this.possibleValues)
    }

    CustomVariable name(String name) {
        setName(name)
        return this
    }

    CustomVariable required(boolean required) {
        setRequired(required)
        return this
    }

    CustomVariable possibleValues(List<T> possibleValues) {
        ListUtils.copyInto(possibleValues, this.possibleValues)
        return this
    }

}
