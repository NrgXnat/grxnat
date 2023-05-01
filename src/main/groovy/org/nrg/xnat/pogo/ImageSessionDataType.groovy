package org.nrg.xnat.pogo

import groovy.transform.InheritConstructors

@InheritConstructors
class ImageSessionDataType extends DataType {

    @Override
    DataType getParent() {
        IMAGE_SESSION
    }

}
