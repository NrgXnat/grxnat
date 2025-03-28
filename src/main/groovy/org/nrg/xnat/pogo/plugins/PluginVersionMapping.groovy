package org.nrg.xnat.pogo.plugins

import com.fasterxml.jackson.annotation.JsonValue

class PluginVersionMapping {
    
    String pluginId
    
    @JsonValue
    Map<String, List<String>> pluginToXnatVersions
    
}
