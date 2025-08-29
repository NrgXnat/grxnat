package org.nrg.xnat.subinterfaces

import org.nrg.xnat.meta.RequirePlugin
import org.nrg.xnat.pogo.PluginRegistry

@RequirePlugin(PluginRegistry.PIXI_ID)
class PixiPluginSubinterface extends XnatPluginSubinterface {
    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/pixi/biodistribution'
        ]
    }

    Map<String, String> uploadBiodistributionData(String projectId, String filePath, String dataOverlapHandling) {
        queryBase()
                .queryParams([
                    'cachePath': filePath,
                    'project': projectId,
                    'dataOverlapHandling': dataOverlapHandling
                ]).post(formatXapiUrl('/pixi/biodistribution/create'))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .as(Map<String, String>)
    }

    void failDataFormattingBiodistributionData(String projectId, String filePath, String dataOverlapHandling) {
        queryBase()
                .queryParams([
                        'cachePath': filePath,
                        'project': projectId,
                        'dataOverlapHandling': dataOverlapHandling
                ]).post(formatXapiUrl('/pixi/biodistribution/create'))
                .then()
                .assertThat()
                .statusCode(400);
    }

    void failAuthorizationBiodistributionData(String projectId, String filePath, String dataOverlapHandling) {
        queryBase()
                .queryParams([
                        'cachePath': filePath,
                        'project': projectId,
                        'dataOverlapHandling': dataOverlapHandling
                ]).post(formatXapiUrl('/pixi/biodistribution/create'))
                .then()
                .assertThat()
                .statusCode(401);
    }
}
