package org.nrg.xnat.subinterfaces

import org.nrg.xnat.versions.XnatVersion
import org.reflections.Reflections

class XnatSubinterfaceVersionManager {

    static final Map<Class<? extends XnatVersion>, List<Class<? extends XnatFunctionalitySubinterface>>> VERSION_SUBINTERFACES = [:]

    static <X extends XnatFunctionalitySubinterface> Class<? extends X> lookupSubinterface(Class<? extends XnatVersion> versionClass, Class<X> baseSubinterfaceClass) {
        VERSION_SUBINTERFACES.putIfAbsent(versionClass, [])
        final List<Class<? extends XnatFunctionalitySubinterface>> subinterfacesForVersion = VERSION_SUBINTERFACES.get(versionClass)
        final Class<? extends X> previouslyMatchedSubinterfaceClass = subinterfacesForVersion.find {
            baseSubinterfaceClass.isAssignableFrom(it)
        } as Class<? extends X>
        if (previouslyMatchedSubinterfaceClass != null) {
            return previouslyMatchedSubinterfaceClass
        }
        final Class<? extends X> newlyMatchedSubinterfaceClass = new Reflections('org.nrg.xnat.subinterfaces').getSubTypesOf(baseSubinterfaceClass).find {
            versionClass in it.newInstance().supportedVersions()
        } ?: baseSubinterfaceClass
        subinterfacesForVersion << newlyMatchedSubinterfaceClass
        newlyMatchedSubinterfaceClass
    }

}
