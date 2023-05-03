package org.nrg.xnat.subinterfaces

import io.restassured.builder.RequestSpecBuilder
import io.restassured.response.ExtractableResponse
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.search.SearchColumnResponse
import org.nrg.xnat.pogo.search.SearchResponse
import org.nrg.xnat.pogo.search.SearchElement
import org.nrg.xnat.pogo.search.XnatSearchDocument
import org.nrg.xnat.pogo.search.XnatSearchParams
import org.nrg.xnat.rest.SerializationUtils

class SearchSubinterface extends XnatFunctionalitySubinterface {

    public static final String RESULT_SET = 'ResultSet'

    @Override
    List<String> getHandledEndpoints() {
        [
                '/projects/{PROJECT_ID}/searches/{SEARCH_ID}',
                '/search',
                '/search/{CACHED_SEARCH_ID}',
                '/search/saved/{SEARCH_ID}'
        ]
    }

    XnatSearchDocument getDefaultSearch(Project project, DataType dataType) {
        retrieveDefaultSearchXml(formatRestUrl("/projects/${project.id}/searches/@${dataType.xsiType}"))
    }

    XnatSearchDocument getDefaultSearch(DataType dataType) {
        retrieveDefaultSearchXml(formatRestUrl("/search/saved/@${dataType.xsiType}"))
    }

    SearchResponse cacheSearch(XnatSearchDocument searchDocument) {
        performSearch(
                searchDocument,
                new XnatSearchParams().cache(true).refresh(true)
        )
    }

    SearchResponse performSearch(XnatSearchDocument searchDocument, XnatSearchParams searchParams = new XnatSearchParams()) {
        jsonQuery()
                .queryParams(SerializationUtils.serializeToMap(searchParams))
                .body(searchDocument.searchXml)
                .post(formatRestUrl('search'))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .jsonPath()
                .getObject(RESULT_SET, SearchResponse)
    }

    String retrieveCachedSearchResultsAsXList(SearchResponse cachedSearch, XnatSearchParams searchParams) {
        retrieveSearch(cachedSearch, searchParams, 'xList').asString()
    }

    SearchResponse retrieveCachedSearchResults(SearchResponse cachedSearch, XnatSearchParams searchParams) {
        retrieveSearch(cachedSearch, searchParams, 'json')
                .jsonPath()
                .getObject(RESULT_SET, SearchResponse)
    }

    List<SearchElement> readSearchElements() {
        jsonQuery()
                .get(formatRestUrl('search', 'elements'))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .jsonPath()
                .getList("${RESULT_SET}.Result", SearchElement)
    }

    SearchColumnResponse retrieveCachedSearchColumn(SearchResponse cachedSearch, String columnName) {
        retrieveCachedSearchColumn(cachedSearch.id, columnName)
    }

    SearchColumnResponse retrieveCachedSearchColumn(String cachedSearchId, String columnName) {
        jsonQuery()
                .get(formatRestUrl("/search/${cachedSearchId}/${columnName}"))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .jsonPath()
                .getObject(RESULT_SET, SearchColumnResponse)
    }

    protected ExtractableResponse retrieveSearch(SearchResponse cachedSearch, XnatSearchParams searchParams, String format) {
        queryBase()
                .queryParams(SerializationUtils.serializeToMap(searchParams))
                .queryParam('format', format)
                .get(formatRestUrl('search', cachedSearch.id))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
    }

    protected XnatSearchDocument retrieveDefaultSearchXml(String url) {
        new XnatSearchDocument(
                queryBase()
                        .spec(new RequestSpecBuilder().setUrlEncodingEnabled(false).build())
                        .get(url)
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .and()
                        .extract()
                        .asString()
        )
    }

}
