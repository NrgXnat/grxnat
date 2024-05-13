package org.nrg.xnat.versions.plugins

import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.nrg.xnat.pogo.XnatPlugin
import org.nrg.xnat.versions.Version

@EqualsAndHashCode
@Builder(builderStrategy = SimpleStrategy, prefix = '')
class GeneralPluginRequirement {

    String pluginId
    String minimumPluginVersion
    String maximumPluginVersion

    static GeneralPluginRequirement fromString(String pluginIdAndVersion) {
        final List<String> pluginStringComponents = pluginIdAndVersion.split(':')
        final String pluginId = pluginStringComponents[0]
        if (pluginStringComponents.size() > 2) {
            throw new RuntimeException("Unexpected format for plugin dependency. Expected something of the form PLUGINID or PLUGINID:OPTIONAL_VERSION, but got ${pluginIdAndVersion}")
        }
        final String requestedVersion = pluginStringComponents.size() == 2 ? pluginStringComponents[1] : null
        new GeneralPluginRequirement(
                pluginId: pluginId,
                minimumPluginVersion: requestedVersion
        )
    }

    PluginDependencyCheck checkAgainst(List<XnatPlugin> installedPlugins) {
        final XnatPlugin installedPlugin = installedPlugins.find { plugin ->
            plugin.id == pluginId
        }
        if (!installedPlugin) {
            return PluginDependencyCheck.MISSING_PLUGIN
        }

        checkAgainst(installedPlugin)
    }

    PluginDependencyCheck checkAgainst(XnatPlugin installedPlugin) {
        final Version installedPluginVersion = new Version(installedPlugin)

        if (minimumPluginVersion) {
            if (installedPluginVersion.lessThan(new Version(minimumPluginVersion))) {
                return new PluginDependencyCheck(
                        state: PluginDependencyCheckState.VERSION_MISMATCH,
                        failureReason: formatReason(pluginId, installedPluginVersion, '>=', minimumPluginVersion)
                )
            }
        }
        if (maximumPluginVersion) {
            if (installedPluginVersion.greaterThanOrEqualTo(new Version(maximumPluginVersion))) {
                return new PluginDependencyCheck(
                        state: PluginDependencyCheckState.VERSION_MISMATCH,
                        failureReason: formatReason(pluginId, installedPluginVersion, '<', maximumPluginVersion)
                )
            }
        }
        return PluginDependencyCheck.SATISFIED
    }

    private static String formatReason(String id, Version installedVersion, String operator, String requiredVersion) {
        "plugin with id '${id}' was found with installed version of ${installedVersion.get()} which does not satisfy the checked requirement to be ${operator} ${requiredVersion}"
    }

}