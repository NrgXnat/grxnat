package org.nrg.xnat.subinterfaces

import org.nrg.xnat.versions.XnatVersion
import org.nrg.xnat.versions.XnatVersionList

class BatchLaunchSubinterface_1_7_7 extends BatchLaunchSubinterface {

    @Override
    List<Class<? extends XnatVersion>> supportedVersions() {
        XnatVersionList.VERSIONS_BEFORE_1_8
    }

    @Override
    Map<String, Object> makeFilterMap(String wrapperName) {
        final Map<String, String> filterVals = new HashMap<>()
        filterVals.put("like", wrapperName)
        filterVals.put("type", "string")
        return Collections.singletonMap("pipelineName", filterVals)
    }
}
