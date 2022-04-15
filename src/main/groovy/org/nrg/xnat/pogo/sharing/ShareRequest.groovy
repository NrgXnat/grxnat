package org.nrg.xnat.pogo.sharing

import org.nrg.xnat.enums.ShareMethod

class ShareRequest {

    ShareMethod operation
    String id
    String destinationProject

}
