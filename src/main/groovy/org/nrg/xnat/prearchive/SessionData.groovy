package org.nrg.xnat.prearchive

import com.fasterxml.jackson.annotation.JsonFormat
import org.nrg.xnat.enums.PrearchiveStatus

class SessionData {
    @JsonFormat(pattern='yyyy-MM-dd HH:mm:ss.SSS') Date uploadDate, lastBuiltDate, scan_date
    String scan_time, subject, url, session, tag, source, visit, protocol, timeZone, timestamp, project, folderName, externalUrl, name, message
    Boolean preventAnon = false
    Boolean preventAutoCommit = false
    Long id
    SessionDataTriple sessionTriple
    PrearchiveStatus status

    class SessionDataTriple {
        String folderName
        String timestamp
        String project
    }

}
