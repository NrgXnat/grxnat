package org.nrg.xnat.pogo

abstract class Extensible<X extends Extensible<X>> {

    private Extension<X> extension

    Extensible(Extension<X> extension) {
        setExtension(extension)
    }

    Extensible() {
        this(null)
    }

    protected void setExtension(Extension<X> extension) {
        this.extension = extension
        if (extension != null && extension.getParentObject() == null) {
            extension.setParentObject(this)
        }
    }

    Extension<X> getExtension() {
        return extension
    }

}
