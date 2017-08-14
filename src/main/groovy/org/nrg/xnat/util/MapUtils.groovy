package org.nrg.xnat.util

class MapUtils {

    static <T, V> void copyInto(Map<T, V> from, Map<T, V> into) {
        into.clear()
        if (from != null && !from.isEmpty()) into.putAll(from)
    }

}
