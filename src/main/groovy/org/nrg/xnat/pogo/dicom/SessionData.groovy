package org.nrg.xnat.pogo.dicom

import com.fasterxml.jackson.annotation.JsonFormat

class SessionData {
    @JsonFormat(pattern='yyyy-MM-dd HH:mm:ss.SSS') Date uploadDate, lastBuiltDate, scan_date
    String scan_time, subject, url, session, tag, source, visit, protocol, timeZone, timestamp, project, folderName, status, externalUrl, name, message
    Boolean preventAnon = false
    Boolean preventAutoCommit = false
    Long id
    SessionDataTriple sessionTriple

    class SessionDataTriple {
        String folderName
        String timestamp
        String project
    }

}
