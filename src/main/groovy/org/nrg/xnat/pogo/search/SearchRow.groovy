package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonIgnore
import org.nrg.xnat.pogo.Project

class SearchRow extends LinkedHashMap<String, String> {

    @JsonIgnore
    String getSessionId() {
        get('session_id')
    }

    String getSubjectLabelInProject(Project project) {
        get("sub_project_identifier_${project.id.toLowerCase()}".toString()) // toString() required, groovy string does not match equals/hashcode
    }

    String getMrSessionLabelInProject(Project project) {
        get("mr_project_identifier_${project.id.toLowerCase()}".toString()) // toString() required, groovy string does not match equals/hashcode
    }

}
