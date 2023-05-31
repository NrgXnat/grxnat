package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonValue

import static org.nrg.xnat.pogo.search.SearchFieldTypes.*

enum ComparisonType {

    EQUALS ('=', ALL_KNOWN_TYPES),
    NOT_EQUALS ('!=', [FLOAT, INTEGER, TIME, TIMESTAMP, DATETIME]),
    LIKE ('LIKE', [STRING]),
    GREATER_THAN ('>', [FLOAT, INTEGER, DATE, TIME, TIMESTAMP, DATETIME]),
    GREATER_THAN_OR_EQUALS ('>=', [FLOAT, INTEGER, DATE, TIME, TIMESTAMP, DATETIME]),
    LESS_THAN ('<', [FLOAT, INTEGER, DATE, TIME, TIMESTAMP, DATETIME]),
    LESS_THAN_OR_EQUALS ('<=', [FLOAT, INTEGER, DATE, TIME, TIMESTAMP, DATETIME]),
    IN ('IN', [FLOAT, INTEGER, TIME, TIMESTAMP, DATETIME]),
    BETWEEN ('BETWEEN', ALL_KNOWN_TYPES),
    IS_NULL ('IS NULL', ALL_KNOWN_TYPES),
    IS_NOT_NULL ('IS NOT NULL', ALL_KNOWN_TYPES)

    private final String xmlRepresentation
    private final List<String> supportedSearchFieldTypes

    ComparisonType(String representation, List<String> comparisons) {
        xmlRepresentation = representation
        supportedSearchFieldTypes = comparisons
    }

    @Override
    @JsonValue
    String toString() {
        xmlRepresentation
    }

    List<String> getSupportedSearchFieldTypes() {
        supportedSearchFieldTypes
    }

}