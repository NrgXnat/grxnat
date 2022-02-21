package org.nrg.xnat.jackson

import org.nrg.xnat.versions.XnatVersion
import org.nrg.xnat.versions.XnatVersionList
import org.reflections.Reflections

class XnatSerializationProviderManager {

    public static final Map<
            Class<? extends XnatSerializationProvider>,
            Map<Class<? extends XnatVersion>, Class<? extends XnatSerializationProvider>>
            > REGISTRY = [:]

    static <X extends XnatSerializationProvider> Class<X> getProviderFor(Class<X> baseProviderClass, Class<XnatVersion> versionClass) {
        if (!REGISTRY.containsKey(baseProviderClass)) {
            final Set<Class<? extends X>> subClasses = new Reflections('org.nrg.xnat.jackson').getSubTypesOf(baseProviderClass)
            REGISTRY.put(baseProviderClass, XnatVersionList.XNAT_VERSION_GRAPH.nodes().collectEntries { xnatVersion ->
                [(xnatVersion) : subClasses.find { xnatVersion in it.newInstance().handledVersions } ?: baseProviderClass]
            })
        }
        REGISTRY[baseProviderClass][versionClass] as Class<X>
    }

}
