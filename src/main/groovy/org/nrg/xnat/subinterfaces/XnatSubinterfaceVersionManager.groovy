package org.nrg.xnat.subinterfaces

import org.nrg.xnat.pogo.XnatPlugin
import org.nrg.xnat.versions.XnatVersion
import org.nrg.xnat.versions.plugins.GeneralPluginRequirement
import org.reflections.Reflections

import java.util.function.Function

class XnatSubinterfaceVersionManager {

    private static final SUBINTERFACE_PACKAGE = 'org.nrg.xnat.subinterfaces'
    static final Map<Class<? extends XnatVersion>, List<Class<? extends CoreXnatFunctionalitySubinterface>>> VERSION_SUBINTERFACES = [:]
    static final Map<String, List<Class<? extends XnatPluginSubinterface>>> PLUGIN_SUBINTERFACES = [:]

    static <X extends CoreXnatFunctionalitySubinterface> Class<? extends X> lookupSubinterface(Class<? extends XnatVersion> versionClass, Class<X> baseSubinterfaceClass) {
        VERSION_SUBINTERFACES.putIfAbsent(versionClass, [])
        retrieveFromCache(
                VERSION_SUBINTERFACES[versionClass],
                baseSubinterfaceClass,
                { Class<? extends X> subclass ->
                    versionClass in subclass.newInstance().supportedVersions()
                }
        )
    }

    static <X extends XnatPluginSubinterface> Class<? extends X> lookupPluginSubinterface(List<XnatPlugin> installedPlugins, Class<X> baseSubinterfaceClass) {
        final GeneralPluginRequirement basePluginRequirement = baseSubinterfaceClass.newInstance().pluginRequirement()
        if (basePluginRequirement.pluginId == null) {
            return baseSubinterfaceClass
        }
        final XnatPlugin installedVersion = installedPlugins.find { plugin ->
            plugin.id == baseSubinterfaceClass.newInstance().pluginRequirement().pluginId
        }
        if (installedVersion == null) {
            return baseSubinterfaceClass
        }
        final String installedVersionAsString = "${installedVersion.id}:${installedVersion.version}".toString()
        PLUGIN_SUBINTERFACES.putIfAbsent(installedVersionAsString, [])
        retrieveFromCache(
                PLUGIN_SUBINTERFACES[installedVersionAsString],
                baseSubinterfaceClass,
                { Class<? extends X> subclass ->
                    subclass.newInstance().pluginRequirement().pluginId(basePluginRequirement.pluginId).checkAgainst(installedVersion)
                }
        )
    }

    private static <X extends XnatFunctionalitySubinterface> Class<? extends X> retrieveFromCache(List<Class<? extends XnatFunctionalitySubinterface>> subinterfaceCache, Class<X> baseSubinterfaceClass, Function<Class<? extends X>, Boolean> requirementCheck) {
        final Class<? extends X> previouslyMatchedSubinterfaceClass = subinterfaceCache.find {
            baseSubinterfaceClass.isAssignableFrom(it)
        } as Class<? extends X>
        if (previouslyMatchedSubinterfaceClass != null) {
            return previouslyMatchedSubinterfaceClass
        }
        final Class<? extends X> newlyMatchedSubinterfaceClass = new Reflections(SUBINTERFACE_PACKAGE).getSubTypesOf(baseSubinterfaceClass)
                .find{ requirementCheck.apply(it) } ?: baseSubinterfaceClass
        subinterfaceCache << newlyMatchedSubinterfaceClass
        newlyMatchedSubinterfaceClass
    }

}
