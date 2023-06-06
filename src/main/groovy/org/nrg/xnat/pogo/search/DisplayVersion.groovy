package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonIgnore

class DisplayVersion {

    String name
    String orderBy
    String darkColor
    String lightColor
    SortOrder defaultSortOrder
    List<DisplayVersionField> fields

    @JsonIgnore
    List<DisplayVersionField> visibleFields() {
        fields.findAll { field ->
            field.visible
        }
    }

}
