package org.nrg.xnat.pogo

abstract class Extension<X extends Extensible<X>> {

    private X parentObject

    Extension(X parentObject) {
        setParentObject(parentObject)
    }

    Extension() {
        this(null)
    }

    void setParentObject(Extensible<X> parentObject) {
        this.parentObject = (X)parentObject
        if (parentObject != null && parentObject.getExtension() == null) {
            parentObject.setExtension(this)
        }
    }

    X getParentObject() {
        parentObject
    }

}
