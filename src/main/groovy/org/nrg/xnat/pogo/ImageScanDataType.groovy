package org.nrg.xnat.pogo

import groovy.transform.InheritConstructors

@InheritConstructors
class ImageScanDataType extends DataType {

    @Override
    DataType getParent() {
        IMAGE_SCAN
    }

}
