package org.nrg.xnat.importer.params

trait SrcRequest<X extends SrcRequest<X>> {

    String src

    X src(String src) {
        setSrc(src)
        this as X
    }

    void verifyData() {
        if (src == null) {
            throw new UnsupportedOperationException('Property src must be specified.')
        }
    }

}