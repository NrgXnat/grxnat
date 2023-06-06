package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonProperty

class SearchFieldList {

    @JsonProperty('Result') List<DisplayField> result
    @JsonProperty('element_name') String elementName
    String title
    @Delegate(includes = [
            'detailedListing',
            'allListing'
    ])
    DisplayVersions displayVersions

    @JsonProperty('versions')
    private void unpackDisplayVersions(Map<String, DisplayVersions> displayVersionsMap) {
        displayVersions = displayVersionsMap['DisplayVersions']
    }

}
