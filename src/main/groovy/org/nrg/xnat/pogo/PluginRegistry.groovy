package org.nrg.xnat.pogo

import org.nrg.xnat.pogo.plugins.BitbucketDownloadsDerivation
import org.nrg.xnat.pogo.plugins.DerivationWithFallback
import org.nrg.xnat.pogo.plugins.GithubReleaseDerivation

class PluginRegistry {

    public static final String XNAT_MAVEN_SERVER = 'https://nrgxnat.jfrog.io/nrgxnat'
    public static final String CS_PLUGIN_ID = 'containers'
    public static final String CS_REPO = 'container-service'
    public static final String BATCH_SHARE_ID = 'batchSharePlugin'
    public static final String DQR_ID = 'dicom-query-retrieve'
    public static final String OHIF_VIEWER_ID = 'ohifViewerPlugin'
    public static final String PIXI_ID = 'PIXIPlugin'
    public static final String XNAT_PLUGIN_GROUP = 'org.nrg.xnat.plugin'
    public static final String XNATX_PLUGIN_GROUP = 'org.nrg.xnatx.plugins'
    public static final String XNAT_DEV = 'xnatdev'
    public static final XnatPlugin CONTAINER_SERVICE = new XnatPlugin(
            CS_PLUGIN_ID,
            CS_PLUGIN_ID,
            XNATX_PLUGIN_GROUP,
            CS_REPO,
            "https://bitbucket.org/${XNAT_DEV}/${CS_REPO}"
    ).withDownloadUrlDerivation(
        new DerivationWithFallback([
                new BitbucketDownloadsDerivation(XNAT_DEV, CS_REPO),
                new GithubReleaseDerivation('NrgXnat', CS_REPO)
        ])
    )
    public static final XnatPlugin FREESURFER_COMMON = new XnatPlugin(
            'nrg_plugin_freesurfercommon',
            'XNAT 1.7 FreeSurfer Common Plugin',
            XNAT_PLUGIN_GROUP,
            'freesurfer-common',
            'https://bitbucket.org/nrg_customizations/nrg_plugin_freesurfercommon'
    )
    public static final XnatPlugin WMH = new XnatPlugin(
            'nrg_plugin_wmh',
            'XNAT 1.7 White Matter Hypointensities Plugin',
            XNAT_PLUGIN_GROUP,
            'wmh',
            'https://bitbucket.org/nrg_customizations/nrg_plugin_wmh'
    )
    public static final XnatPlugin RAD_READ = new XnatPlugin(
            'radreadPlugin',
            'XNAT 1.7 Rad Read Plugin',
            XNAT_PLUGIN_GROUP,
            'rad-read',
            'https://bitbucket.org/nrg_customizations/nrg_plugin_radread'
    )
    public static final XnatPlugin BATCH_SHARE = new XnatPlugin(
            BATCH_SHARE_ID,
            'Batch Share Plugin',
            'com.radiologics.plugins',
            'batch-share',
            'https://bitbucket.org/radiologics/batch-share-plugin'
    )
    public static final XnatPlugin PIXI = new XnatPlugin(
            PIXI_ID,
            "PIXI Plugin",
            XNAT_PLUGIN_GROUP,
            'pixi-plugin',
            'https://github.com/preclinical-imaging/pixi-plugin'
    )
    public static final XnatPlugin NIHCC = new XnatPlugin(
            'nihcc-xnat',
            'XNAT 1.7 NIHCC XRay Plugin',
            XNAT_PLUGIN_GROUP,
            'nihcc',
            'https://bitbucket.org/cmoore01/nihcc-xnat-plugin'
    )
    public static final XnatPlugin DQR = new XnatPlugin(
            DQR_ID,
            'DICOM Query Retrieve Plugin',
            XNATX_PLUGIN_GROUP,
            DQR_ID,
            "https://bitbucket.org/${XNAT_DEV}/${DQR_ID}"
    ).withDownloadUrlDerivation(new BitbucketDownloadsDerivation(XNAT_DEV, DQR_ID))
    public static final XnatPlugin OHIF_VIEWER = new XnatPlugin(
            OHIF_VIEWER_ID,
            'XNAT OHIF Viewer Plugin',
            null,
            null,
            'https://bitbucket.org/icrimaginginformatics/ohif-viewer-xnat-plugin'
    )
    public static final List<XnatPlugin> KNOWN_PLUGINS = [CONTAINER_SERVICE, FREESURFER_COMMON, WMH, RAD_READ, BATCH_SHARE, NIHCC]

    static XnatPlugin lookupPlugin(String id) {
        KNOWN_PLUGINS.find { plugin ->
            plugin.id == id
        }
    }

}
