package org.nrg.xnat.importer.params

import org.nrg.xnat.enums.MergeBehavior

trait OverwriteRequest<X extends OverwriteRequest<X>> {

    MergeBehavior overwrite

    X overwrite(MergeBehavior behavior) {
        setOverwrite(behavior)
        this as X
    }

}