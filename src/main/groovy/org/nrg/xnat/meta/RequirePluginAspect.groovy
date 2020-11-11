package org.nrg.xnat.meta

import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.nrg.xnat.interfaces.XnatInterface

@Aspect
class RequirePluginAspect {

    @Before('target(xnatInterface) && @annotation(pluginRequirement)')
    void checkPlugins(XnatInterface xnatInterface, RequirePlugin pluginRequirement) throws Throwable {
        if (!xnatInterface.readInstalledPlugins()*.id.contains(pluginRequirement.value())) {
            throw new UnsupportedOperationException("You attempted to perform a call depending on the '${pluginRequirement.value()}' plugin, but it is not installed on the XNAT instance.")
        }
    }

}
