package org.nrg.xnat.pogo.dicomweb

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode

@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(excludes = 'number')
class DicomPersonNames {

    @JsonProperty('Alphabetic') String alphabetic
    @JsonProperty('Ideographic') String ideographic
    @JsonProperty('Phonetic') String phonetic
    @JsonIgnore int number

    DicomPersonNames alphabetic(String name) {
        setAlphabetic(name)
        this
    }

    DicomPersonNames ideographic(String name) {
        setIdeographic(name)
        this
    }

    DicomPersonNames phonetic(String name) {
        setPhonetic(name)
        this
    }

}
