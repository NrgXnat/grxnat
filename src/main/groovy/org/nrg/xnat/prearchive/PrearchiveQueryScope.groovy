package org.nrg.xnat.prearchive

import org.nrg.xnat.pogo.Project

abstract class PrearchiveQueryScope {

    abstract List<String> getUrlFragments()

    public static final PrearchiveQueryScope FULL_PREARCHIVE = new PrearchiveQueryScope() {
        @Override
        List<String> getUrlFragments() {
            ['prearchive']
        }
    }

    public static final PrearchiveQueryScope UNASSIGNED = forProject(new Project('Unassigned'))

    static final PrearchiveQueryScope forProject(Project project) {
        new PrearchiveQueryScope() {
            @Override
            List<String> getUrlFragments() {
                ['prearchive', 'projects', project.id]
            }
        }
    }

}
