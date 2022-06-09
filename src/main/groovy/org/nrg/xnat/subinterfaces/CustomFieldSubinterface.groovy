package org.nrg.xnat.subinterfaces

import io.restassured.http.ContentType
import org.nrg.xnat.pogo.CustomFieldScope


class CustomFieldSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/custom-fields/experiments/{experiment}/assessors/{assessor}/fields',
                '/xapi/custom-fields/experiments/{experiment}/assessors/{assessor}/fields/{key}',
                '/xapi/custom-fields/experiments/{experiment}/fields',
                '/xapi/custom-fields/experiments/{experiment}/fields/{key}',
                '/xapi/custom-fields/experiments/{experiment}/scans/{scan}/fields',
                '/xapi/custom-fields/experiments/{experiment}/scans/{scan}/fields/{key}',
                '/xapi/custom-fields/projects/{project}/experiments/{experiment}/assessors/{assessor}/fields',
                '/xapi/custom-fields/projects/{project}/experiments/{experiment}/assessors/{assessor}/fields/{key}',
                '/xapi/custom-fields/projects/{project}/experiments/{experiment}/fields',
                '/xapi/custom-fields/projects/{project}/experiments/{experiment}/fields/{key}',
                '/xapi/custom-fields/projects/{project}/experiments/{experiment}/scans/{scan}/fields',
                '/xapi/custom-fields/projects/{project}/experiments/{experiment}/scans/{scan}/fields/{key}',
                '/xapi/custom-fields/projects/{project}/fields',
                '/xapi/custom-fields/projects/{project}/fields/{key}',
                '/xapi/custom-fields/projects/{project}/subjects/{subject}/experiments/{experiment}/assessors/{assessor}/fields',
                '/xapi/custom-fields/projects/{project}/subjects/{subject}/experiments/{experiment}/assessors/{assessor}/fields/{key}',
                '/xapi/custom-fields/projects/{project}/subjects/{subject}/experiments/{experiment}/fields',
                '/xapi/custom-fields/projects/{project}/subjects/{subject}/experiments/{experiment}/fields/{key}',
                '/xapi/custom-fields/projects/{project}/subjects/{subject}/experiments/{experiment}/scans/{scan}/fields',
                '/xapi/custom-fields/projects/{project}/subjects/{subject}/experiments/{experiment}/scans/{scan}/fields/{key}',
                '/xapi/custom-fields/projects/{project}/subjects/{subject}/fields',
                '/xapi/custom-fields/projects/{project}/subjects/{subject}/fields/{key}',
                '/xapi/custom-fields/subjects/{subject}/fields',
                '/xapi/custom-fields/subjects/{subject}/fields/{key}'
        ]
    }

    Map<String, Object> readCustomFields(CustomFieldScope fieldScope, List<String> fieldSubset = null) {
        final Map<String, Object> queryParams = (fieldSubset != null) ? ['keys': fieldSubset.join(',')] : [:]
        queryBaseWithStatusCodeListeners([NOT_FOUND_404]).queryParams(queryParams).get(formatXapiUrl(fieldScope.buildUriFragment(), 'fields')).then().
                assertThat().statusCode(200).and().extract().as(Map)
    }

    String readCustomField(CustomFieldScope fieldScope, String fieldName) {
        queryBaseWithStatusCodeListeners([NOT_FOUND_404]).get(formatXapiUrl(fieldScope.buildUriFragment(), 'fields', fieldName)).then().
                assertThat().statusCode(200).and().extract().asString()
    }

    Map<String, Object> setCustomFields(CustomFieldScope fieldScope, Map<String, Object> customFields) {
        queryBaseWithStatusCodeListeners([FORBIDDEN_403, NOT_FOUND_404]).contentType(ContentType.JSON).body(customFields).put(formatXapiUrl(fieldScope.buildUriFragment(), 'fields')).then().
                assertThat().statusCode(200).and().extract().as(Map)
    }

    Map<String, Object> deleteCustomField(CustomFieldScope fieldScope, String fieldName) {
        queryBaseWithStatusCodeListeners([FORBIDDEN_403, NOT_FOUND_404]).delete(formatXapiUrl(fieldScope.buildUriFragment(), 'fields', fieldName)).then().
                assertThat().statusCode(200).and().extract().jsonPath().getMap('')
    }

}
