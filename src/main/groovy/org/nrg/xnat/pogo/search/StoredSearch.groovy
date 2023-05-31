package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.apache.commons.lang3.StringUtils

/**
 * This class overlaps a lot with XnatSearchDocument, but is still useful nonetheless.
 * It mainly is acting as a container for the response to the stored search API
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
class StoredSearch {

    String allowDiffColumns
    String sortByFieldId
    String briefDescription
    String description
    String layeredSequence
    String secure
    String users
    String rootElementName
    String id
    String tag
    String sortByElementName
    XnatSearchDocument searchDocument

    List<String> parseUsernames() {
        StringUtils.strip(users, '{}').split(',')
    }

}
