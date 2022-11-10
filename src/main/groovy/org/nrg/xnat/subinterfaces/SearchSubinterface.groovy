package org.nrg.xnat.subinterfaces

import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.search.CachedSearch
import org.nrg.xnat.pogo.search.XnatSearchDocument
import org.nrg.xnat.pogo.search.XnatSearchParams
import org.nrg.xnat.rest.SerializationUtils

class SearchSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/projects/{PROJECT_ID}/searches/{SEARCH_ID}',
                '/search',
                '/search/{CACHED_SEARCH_ID}'
        ]
    }

    XnatSearchDocument getDefaultSearch(Project project, DataType dataType) {
        final String xml = queryBase().spec(new RequestSpecBuilder().setUrlEncodingEnabled(false).build()).get(formatRestUrl('/projects', project.id, 'searches', "@${dataType.xsiType}"))
                .then().assertThat().statusCode(200).and().extract().asString()
        new XnatSearchDocument(searchXml: xml)
    }

    CachedSearch cacheSearch(XnatSearchDocument searchDocument) {
        jsonQuery().queryParams('cache': true, 'refresh': true).body(searchDocument.searchXml).post(formatRestUrl('search'))
                .then().assertThat().statusCode(200).and().extract().jsonPath().getObject('ResultSet', CachedSearch)
    }

    String retrieveCachedSearchResultsAsXList(CachedSearch cachedSearch, XnatSearchParams searchParams) {
        queryBase().queryParam('format', 'xList').queryParams(SerializationUtils.serializeToMap(searchParams)).get(formatRestUrl('search', cachedSearch.id))
                .then().assertThat().statusCode(200).and().extract().asString() // TODO: generalize to allow more useful formats
    }

}
