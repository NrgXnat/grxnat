package org.nrg.xnat.subinterfaces

import io.restassured.http.ContentType
import io.restassured.response.Response
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.features.FeatureUpdateBody
import org.nrg.xnat.pogo.features.FeaturesSpec

class FeaturesSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        ['/services/features']
    }

    FeaturesSpec readFeaturesForSite() {
        assertAndParse(queryBase().queryParam('type', 'admin').get(featuresEndpoint()))
    }

    FeaturesSpec readFeaturesForProject(Project project) {
        assertAndParse(queryBase().queryParam('tag', project.id).get(featuresEndpoint()))
    }

    XnatInterface banFeature(String featureKey) {
        updateFeature(new FeatureUpdateBody(key: featureKey, banned: true))
    }

    XnatInterface unbanFeature(String featureKey) {
        updateFeature(new FeatureUpdateBody(key: featureKey, banned: false))
    }

    XnatInterface enableFeatureByDefault(String featureKey) {
        updateFeature(new FeatureUpdateBody(key: featureKey, enabled: true))
    }

    XnatInterface disableFeatureByDefault(String featureKey) {
        updateFeature(new FeatureUpdateBody(key: featureKey, enabled: false))
    }

    private FeaturesSpec assertAndParse(Response response) {
        response.then().assertThat().statusCode(200).and().extract().jsonPath().getObject('get(0)', FeaturesSpec)
    }

    private XnatInterface updateFeature(FeatureUpdateBody body, String group = null) {
        final Map<String, Object> queryParams = group != null ? ['group': group] : [:]
        queryBase().contentType(ContentType.JSON).body(body).queryParams(queryParams).post(featuresEndpoint()).
            then().assertThat().statusCode(200)
        xnatInterface
    }

    private String featuresEndpoint() {
        formatRestUrl('/services/features')
    }

}
