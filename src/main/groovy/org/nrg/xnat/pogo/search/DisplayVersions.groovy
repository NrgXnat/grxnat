package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

class DisplayVersions {

    @JsonProperty('element_name') String elementName
    List<DisplayVersion> versions

    @JsonIgnore
    DisplayVersion detailedListing() {
        find('detailed')
    }

    @JsonIgnore
    DisplayVersion allListing() {
        find('all')
    }

    DisplayVersion find(String displayVersionName) {
        versions.find { version ->
            version.name == displayVersionName
        }
    }

}
