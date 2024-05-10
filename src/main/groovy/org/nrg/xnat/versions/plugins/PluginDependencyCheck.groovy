package org.nrg.xnat.versions.plugins

class PluginDependencyCheck {

    PluginDependencyCheckState state
    String failureReason
    public static final PluginDependencyCheck SATISFIED = new PluginDependencyCheck(state: PluginDependencyCheckState.SATISFIED)
    public static final PluginDependencyCheck MISSING_PLUGIN = new PluginDependencyCheck(state: PluginDependencyCheckState.MISSING_PLUGIN)

}
