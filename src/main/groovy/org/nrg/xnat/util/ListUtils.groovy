package org.nrg.xnat.util

class ListUtils {

    static <X> void copyInto(List<X> from, List<X> into) {
        into.clear()
        if (from != null && !from.isEmpty()) into.addAll(from)
    }

    static <X> List<X> copy(List<X> input) {
        final List<X> list = []
        copyInto(input, list)
        return list
    }

}
