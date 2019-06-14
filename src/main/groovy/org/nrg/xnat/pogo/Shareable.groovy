package org.nrg.xnat.pogo

interface Shareable {

    List<Share> getShares()

    String getLabel()

    Project getPrimaryProject()

}
