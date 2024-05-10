package org.nrg.xnat.meta

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.subinterfaces.XnatFunctionalitySubinterface

@Aspect
/*
  Commented advice methods can be uncommented to allow plugin checks for:
    1) Methods directly in XnatInterface, or
    2) Individual methods in an XnatFunctionalitySubinterface class without applying to the whole class
 */
class RequirePluginAspect {

    private static final List<String> methodWhitelist = ['$getStaticMetaClass', 'getClass', '<init>', 'getXnatInterface', 'pluginRequirement']

    //@Before('target(xnatInterface) && @annotation(pluginRequirement)')
    void checkPlugins(XnatInterface xnatInterface, RequirePlugin pluginRequirement) throws Throwable {
        if (!xnatInterface.readInstalledPlugins()*.id.contains(pluginRequirement.value())) {
            throw new UnsupportedOperationException("You attempted to perform a call depending on the '${pluginRequirement.value()}' plugin, but it is not installed on the XNAT instance.")
        }
    }

    //@Before('target(xnatSubinterface) && @annotation(pluginRequirement)')
    void checkPlugins(XnatFunctionalitySubinterface xnatSubinterface, RequirePlugin pluginRequirement) throws Throwable {
        checkPlugins(xnatSubinterface.xnatInterface, pluginRequirement)
    }

    @Before('target(xnatSubinterface) && @within(pluginRequirement)')
    void checkPlugins(XnatFunctionalitySubinterface xnatSubinterface, RequirePlugin pluginRequirement, JoinPoint joinPoint) throws Throwable {
        if (joinPoint.signature.name in methodWhitelist) {
            return
        }
        checkPlugins(xnatSubinterface, pluginRequirement)
    }

}
