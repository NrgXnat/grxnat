package org.nrg.xnat.subinterfaces

import org.nrg.xnat.pogo.PluginRegistry
import org.nrg.xnat.pogo.dqr.DqrSettings
import org.nrg.xnat.pogo.dqr.DqrSettings1x
import org.nrg.xnat.versions.plugins.GeneralPluginRequirement

class DqrSubinterface_1_x extends DqrSubinterface {

    @Override
    GeneralPluginRequirement pluginRequirement() {
        new GeneralPluginRequirement().maximumPluginVersion('2.0').pluginId(PluginRegistry.DQR_ID)
    }

    @Override
    Class<? extends DqrSettings> appropriateDqrSettingsClass() {
        DqrSettings1x
    }

    @Override
    DqrSettings prepareSettingsForPost(DqrSettings dqrSettings) {
        final DqrSettings1x historicalObject = new DqrSettings1x()
        dqrSettings.properties.each { key, val ->
            if (key != 'class') {
                historicalObject.setProperty(key, val)
            }
        }
        historicalObject
    }

}
