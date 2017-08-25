package org.nrg.xnat.enums

enum ContainerType {
    DOCKER       ("docker"),
    SINGULARITY  ("singularity"),
    KUBERNETES   ("kubernetes")

    private final String simpleName

    ContainerType(String simpleName) {
        this.simpleName = simpleName
    }

    String getSimpleName() {
        simpleName
    }

    @Override
    String toString() {
        simpleName
    }

}