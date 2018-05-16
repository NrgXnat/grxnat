package org.nrg.xnat.pogo.custom_variable

import org.nrg.xnat.enums.VariableType
import org.nrg.xnat.util.ListUtils

import java.time.LocalDate

class CustomVariable<T> {

    String name
    boolean required = false
    VariableType type
    private final List<T> possibleValues = []

    CustomVariable(String name) {
        this.name = name
    }

    CustomVariable(Class<T> classToken) {
        switch (classToken) {
            case String:
                type = VariableType.STRING
                break
            case Integer:
                type = VariableType.INTEGER
                break
            case Double:
            case Float:
                type = VariableType.FLOAT
                break
            case Boolean:
                type = VariableType.BOOLEAN
                break
            case LocalDate:
                type = VariableType.DATE
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

    CustomVariable<T> name(String name) {
        setName(name)
        this
    }

    CustomVariable<T> required(boolean required) {
        setRequired(required)
        this
    }

    CustomVariable<T> type(VariableType type) {
        setType(type)
        this
    }

    CustomVariable<T> possibleValues(List<T> possibleValues) {
        ListUtils.copyInto(possibleValues, this.possibleValues)
        this
    }

}
