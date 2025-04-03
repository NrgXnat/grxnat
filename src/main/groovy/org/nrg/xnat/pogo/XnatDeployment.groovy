package org.nrg.xnat.pogo

import com.fasterxml.jackson.databind.ObjectMapper
import org.nrg.testing.FileIOUtils
import org.nrg.xnat.pogo.plugins.PluginVersionMapping
import org.nrg.xnat.versions.XnatVersion
import org.nrg.xnat.versions.XnatVersionList

import java.nio.file.Paths

class XnatDeployment {
    
    String id
    Class<? extends XnatVersion> xnatVersion
    String xnatVersionString
    List<XnatPlugin> plugins = []
    public static final List<PluginVersionMapping> knownPluginMappings = []
    
    static XnatDeployment deploymentFromString(String deploymentKey) {
        final List<String> deploymentComponents = deploymentKey.split('\\+')
        final String xnatVersion = deploymentComponents[0]
        final XnatDeployment deployment = new XnatDeployment()
        deployment.setId(deploymentKey)
        deployment.setXnatVersion(XnatVersionList.lookup(xnatVersion))
        deployment.setXnatVersionString(xnatVersion)
        deploymentComponents.remove(0)
        deployment.setPlugins(deploymentComponents.collect { pluginId ->
            final PluginVersionMapping pluginVersionMapping = lookupPluginMapping(pluginId)
            PluginRegistry.KNOWN_PLUGINS.find { it.id == pluginId }.ofSpecificVersion(
                    pluginVersionMapping.pluginToXnatVersions.find { entry ->
                        entry.value.contains(xnatVersion)
                    }.key
            )
        })
        deployment
    }
    
    static PluginVersionMapping lookupPluginMapping(String pluginId) {
        final PluginVersionMapping foundMapping = knownPluginMappings.find { it.pluginId == pluginId }
        if (foundMapping != null) {
            return foundMapping
        }
        final PluginVersionMapping versionMapping = new ObjectMapper().readValue(
                FileIOUtils.loadResource(Paths.get('plugin_version_mappings', "${pluginId}.json").toString()),
                PluginVersionMapping
        )
        versionMapping.setPluginId(pluginId)
        knownPluginMappings << versionMapping
        versionMapping
    }

    @Override
    String toString() {
        id
    }

}
