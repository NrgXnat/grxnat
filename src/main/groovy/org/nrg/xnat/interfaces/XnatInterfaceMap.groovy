package org.nrg.xnat.interfaces

import org.reflections.Reflections

class XnatInterfaceMap {

    private static final Map<String, Class<? extends XnatInterface>> VERSION_IDENTIFIERS = [:]
    private static final Map<String, Class<? extends XnatInterface>> KNOWN_VERSIONS = [:]

    static Class<? extends XnatInterface> lookup(String fullVersion) {
        if (VERSION_IDENTIFIERS.isEmpty()) {
            new Reflections('org.nrg.xnat.interfaces').getSubTypesOf(XnatInterface).each { subClass ->
                if (subClass != UnitTestXnatInterface) {
                    VERSION_IDENTIFIERS.put(subClass.newInstance().versionIdentifier(), subClass)
                }
            }
        }
        if (!KNOWN_VERSIONS.containsKey(fullVersion)) {
            final String match = VERSION_IDENTIFIERS.keySet().find { identifier ->
                fullVersion.startsWith(identifier)
            }
            KNOWN_VERSIONS.put(fullVersion, (match != null) ? VERSION_IDENTIFIERS[match] : XnatInterface_1_8_0)
        }
        KNOWN_VERSIONS[fullVersion]
    }

}
