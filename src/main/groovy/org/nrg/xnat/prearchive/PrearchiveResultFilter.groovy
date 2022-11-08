package org.nrg.xnat.prearchive

import org.nrg.xnat.enums.PrearchiveStatus

class PrearchiveResultFilter {

    String subject
    String name
    PrearchiveStatus status

    PrearchiveResultFilter subject(String subject) {
        setSubject(subject)
        this
    }

    PrearchiveResultFilter name(String name) {
        setName(name)
        this
    }

    PrearchiveResultFilter status(PrearchiveStatus status) {
        setStatus(status)
        this
    }

    List<SessionData> filter(List<SessionData> results) {
        if (subject != null) {
            results = results.findAll { result ->
                result.subject == subject
            }
        }
        if (name != null) {
            results = results.findAll { result ->
                result.name == name
            }
        }
        if (status != null) {
            results = results.findAll { result ->
                result.status == status
            }
        }
        results
    }

    @Deprecated
    SessionData findUniqueResult(List<SessionData> results) {
        final List<SessionData> filtered = filter(results)
        if (filtered.size() != 1) {
            throw new UnsupportedOperationException("Unexpected number of matched results found: ${filtered.size()}")
        }
        filtered[0]
    }

}
