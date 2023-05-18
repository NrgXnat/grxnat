package org.nrg.xnat.pogo.search

class SearchFieldTypes {

    public static final String STRING = 'string'
    public static final String FLOAT = 'float'
    public static final String INTEGER = 'integer'
    public static final String DATE = 'date'
    public static final String TIME = 'time'
    public static final String TIMESTAMP = 'timestamp'
    public static final List<String> ALL_KNOWN_TYPES = [STRING, FLOAT, INTEGER, DATE, TIME, TIMESTAMP]
    private static final Map<String, List<ComparisonType>> SUPPORTED_COMPARISONS = [:]

    static List<ComparisonType> supportedComparisonsFor(String searchFieldType) {
        cache()
        (searchFieldType in ALL_KNOWN_TYPES) ? SUPPORTED_COMPARISONS[searchFieldType] : (ComparisonType.values() as List<ComparisonType>)
    }

    private static void cache() {
        if (SUPPORTED_COMPARISONS.isEmpty()) {
            ALL_KNOWN_TYPES.each { columnType ->
                SUPPORTED_COMPARISONS.put(columnType, ComparisonType.values().findAll { columnType in it.supportedSearchFieldTypes } as List<ComparisonType>)
            }
        }
    }

}
