package org.nrg.xnat.enums

enum VariableType {
    INTEGER ('integer'),
    FLOAT ('float'),
    DATE ('date'),
    STRING ('string'),
    BOOLEAN ('boolean')

    private final String type

    VariableType(String typeName) {
        type = typeName
    }

    @Override
    String toString() {
        type
    }

}
