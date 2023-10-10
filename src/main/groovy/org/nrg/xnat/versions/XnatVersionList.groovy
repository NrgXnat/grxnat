package org.nrg.xnat.versions

import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import com.google.common.graph.MutableGraph
import org.nrg.xnat.util.GraphUtils
import org.reflections.Reflections

class XnatVersionList {

    public static final List<String> KNOWN_VERSION_KEYS = []
    public static final Map<String, Class<? extends XnatVersion>> KEY_TO_VERSION_MAP = [:]
    public static final MutableGraph<Class<? extends XnatVersion>> XNAT_VERSION_GRAPH = GraphBuilder.directed().allowsSelfLoops(false).build()
    public static final List<Class<? extends XnatVersion>> VERSIONS_BEFORE_1_8 = [Xnat_1_7_7, Xnat_1_7_6, Xnat_1_7_5_2, Xnat_1_7_5, Xnat_1_7_4, Xnat_1_7_3, Xnat_1_7_2, Xnat_1_7_1, Xnat_1_7_0, Xnat_1_6dev]
    private static final Class<? extends XnatVersion> DEFAULT_VERSION = Xnat_1_8_0

    static {
        readXnatVersions()
    }

    static Class<? extends XnatVersion> lookup(String versionString) {
        if (versionString in KEY_TO_VERSION_MAP.keySet()) {
            KEY_TO_VERSION_MAP[versionString]
        } else {
            final Map.Entry<String, Class<? extends XnatVersion>> partialMatch = KEY_TO_VERSION_MAP.find { entry ->
                versionString.contains(entry.key)
            }
            if (partialMatch == null) {
                println("Could not match version '${versionString}', falling back on default XNAT version class of ${DEFAULT_VERSION.simpleName}")
                DEFAULT_VERSION
            } else {
                partialMatch.value
            }
        }
    }

    private static void readXnatVersions() {
        final MutableGraph<String> versionStringGraph = GraphBuilder.directed().allowsSelfLoops(false).build()
        final List<Class<? extends XnatVersion>> xnatVersions = new Reflections('org.nrg.xnat.versions').getSubTypesOf(XnatVersion).toList()

        final Map<Class<? extends XnatVersion>, List<String>> keysForClass = xnatVersions.collectEntries { xnatVersionClass ->
            XNAT_VERSION_GRAPH.addNode(xnatVersionClass)
            final List<String> keys = xnatVersionClass.newInstance().versionKeys
            keys.each { versionKey ->
                versionStringGraph.addNode(versionKey)
            }
            [(xnatVersionClass) : keys]
        }

        xnatVersions.each { xnatVersion ->
            final List<String> versionKeys = keysForClass[xnatVersion]
            versionKeys.eachWithIndex { versionKey, index ->
                KEY_TO_VERSION_MAP.put(versionKey, xnatVersion)
                if (index < versionKeys.size() - 1) {
                    versionStringGraph.putEdge(versionKey, versionKeys[index + 1]) // maintain internal order in the versionKeys() list
                }
            }

            final Follows follows = xnatVersion.getAnnotation(Follows)
            if (follows != null) {
                follows.value().each { precedingXnatVersion ->
                    XNAT_VERSION_GRAPH.putEdge(precedingXnatVersion, xnatVersion)
                    versionStringGraph.putEdge(keysForClass[precedingXnatVersion][0], versionKeys[0])

                    keysForClass[precedingXnatVersion].each { precedingKey ->
                        versionKeys.each { versionKey ->
                            versionStringGraph.putEdge(precedingKey, versionKey)
                        }
                    }
                }
            }
        }

        KNOWN_VERSION_KEYS.addAll(GraphUtils.topologicalSort(versionStringGraph))
        println("Known XNAT versions at runtime: ${KNOWN_VERSION_KEYS.join(', ')}")
    }

    static boolean firstFollowsSecond(Class<? extends XnatVersion> first, Class<? extends XnatVersion> second) {
        (first != second) && first in Graphs.reachableNodes(XNAT_VERSION_GRAPH, second)
    }

    static List<Class<? extends XnatVersion>> knownVersionsBefore(Class<? extends XnatVersion> version) {
        XNAT_VERSION_GRAPH.nodes().findAll { candidateVersion ->
            firstFollowsSecond(version, candidateVersion)
        }
    }

}
