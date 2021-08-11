package org.nrg.xnat.pogo

class PluginRegistry {

    public static final String XNAT_MAVEN_SERVER = 'https://nrgxnat.jfrog.io/nrgxnat'
    public static final String CS_PLUGIN_ID = 'containers'
    public static final XnatPlugin CONTAINER_SERVICE = new XnatPlugin(CS_PLUGIN_ID, 'containers', 'org.nrg.xnatx.plugins', 'container-service', 'https://bitbucket.org/xnatdev/container-service')
    public static final XnatPlugin FREESURFER_COMMON = new XnatPlugin('nrg_plugin_freesurfercommon', 'XNAT 1.7 FreeSurfer Common Plugin', 'org.nrg.xnat.plugin', 'freesurfer-common', 'https://bitbucket.org/nrg_customizations/nrg_plugin_freesurfercommon')
    public static final XnatPlugin WMH = new XnatPlugin('nrg_plugin_wmh', 'XNAT 1.7 White Matter Hypointensities Plugin', 'org.nrg.xnat.plugin', 'wmh', 'https://bitbucket.org/nrg_customizations/nrg_plugin_wmh')
    public static final XnatPlugin RAD_READ = new XnatPlugin('radreadPlugin', 'XNAT 1.7 Rad Read Plugin', 'org.nrg.xnat.plugin', 'rad-read', 'https://bitbucket.org/nrg_customizations/nrg_plugin_radread')
    public static final XnatPlugin NIHCC = new XnatPlugin('nihcc-xnat', 'XNAT 1.7 NIHCC XRay Plugin', 'org.nrg.xnat.plugin', 'nihcc', 'https://bitbucket.org/cmoore01/nihcc-xnat-plugin')
    public static final List<XnatPlugin> KNOWN_PLUGINS = [CONTAINER_SERVICE, FREESURFER_COMMON, WMH, RAD_READ, NIHCC]

    static XnatPlugin lookupPlugin(String id) {
        KNOWN_PLUGINS.find { plugin ->
            plugin.id == id
        }
    }

}
