package org.nrg.xnat.pogo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

class PaginatedRequest {

    @JsonProperty('sort_col') Object sortColumn
    @JsonProperty('sort_dir') SortDir sortDir;
    int size
    int page
    String id
    Map<Object, QueryFilter> filters = new HashMap<>()

    PaginatedRequest page(int page) {
        this.page = page
        return this
    }

    PaginatedRequest size(int size) {
        this.size = size
        return this
    }

    PaginatedRequest sortColumn(Object sortColumn) {
        this.sortColumn = sortColumn
        return this
    }

    PaginatedRequest sortDir(SortDir sortDir) {
        this.sortDir = sortDir
        return this
    }

    PaginatedRequest sort(Object sortColumn, SortDir sortDir) {
        this.sortColumn = sortColumn
        this.sortDir = sortDir
        return this
    }

    PaginatedRequest filter(Object key, QueryFilter filter) {
        this.filters.put(key, filter)
        return this
    }

    public enum SortDir {
        DESC("desc"),
        ASC("asc");

        String direction;

        SortDir(String direction) {
            this.direction = direction;
        }

        @JsonValue
        public String getDirection() {
            return direction;
        }
    }
}
