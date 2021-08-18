package org.nrg.xnat.importer.params

import com.fasterxml.jackson.annotation.JsonIgnore

trait FileRequest<X extends FileRequest<X>> extends SrcRequest<X> {

    File file

    X file(File file) {
        setFile(file)
        this as X
    }

    @JsonIgnore
    File getFile() {
        file
    }

    @Override
    void verifyData() {
        if ((file == null && src == null) || (file != null && src != null)) {
            throw new UnsupportedOperationException('Exactly one of src or file must be specified.')
        }
    }

}