package org.nrg.xnat.importer.params

trait RenameRequest<X extends RenameRequest<X>> {

    Boolean rename

    X rename(Boolean setting = false) {
        setRename(setting)
        this as X
    }

}