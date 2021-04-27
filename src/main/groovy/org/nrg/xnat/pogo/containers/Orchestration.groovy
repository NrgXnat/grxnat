package org.nrg.xnat.pogo.containers

class Orchestration {
    long id = 0L
    String name
    List<Long> wrapperIds
    boolean enabled = true

    Orchestration() {}
    Orchestration(String name, List<Long> wrapperIds) {
        this.name = name
        this.wrapperIds = wrapperIds
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Orchestration that = (Orchestration) o

        if (enabled != that.enabled) return false
        if (id != that.id) return false
        if (name != that.name) return false
        if (wrapperIds != that.wrapperIds) return false

        return true
    }

    int hashCode() {
        int result
        result = (int) (id ^ (id >>> 32))
        result = 31 * result + name.hashCode()
        result = 31 * result + (wrapperIds != null ? wrapperIds.hashCode() : 0)
        result = 31 * result + (enabled ? 1 : 0)
        return result
    }
}
