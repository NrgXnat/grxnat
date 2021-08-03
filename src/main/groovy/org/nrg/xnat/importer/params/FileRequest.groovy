package org.nrg.xnat.importer.params

import com.fasterxml.jackson.annotation.JsonIgnore

trait FileRequest<X extends FileRequest<X>> {

    File file
    String src

    X file(File file) {
        setFile(file)
        this as X
    }

    X src(String src) {
        setSrc(src)
        this as X
    }

    @JsonIgnore
    File getFile() {
        file
    }

    void verifyData() {
        if ((file == null && src == null) || (file != null && src != null)) {
            throw new UnsupportedOperationException('Exactly one of src or file must be specified.')
        }
    }

}