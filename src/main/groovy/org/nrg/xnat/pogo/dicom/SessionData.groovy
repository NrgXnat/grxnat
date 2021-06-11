package org.nrg.xnat.pogo.dicom

import com.fasterxml.jackson.annotation.JsonFormat

class SessionData {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
    private Date              uploadDate
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
    private Date              lastBuiltDate
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
    private Date              scan_date

    private String            scan_time, subject, url, session, tag, source, visit, protocol, timeZone, timestamp, project, folderName, status, externalUrl, name
    private Boolean        preventAnon       = false
    private Boolean        preventAutoCommit = false
    private Long id
    private SessionDataTriple sessionTriple

    public class SessionDataTriple {
        private String folderName;
        private String timestamp;
        private String project;
    }

    Date getUploadDate() {
        return uploadDate
    }

    Date getLastBuiltDate() {
        return lastBuiltDate
    }

    Date getScan_date() {
        return scan_date
    }

    String getScan_time() {
        return scan_time
    }

    String getSubject() {
        return subject
    }

    String getUrl() {
        return url
    }

    String getSession() {
        return session
    }

    String getTag() {
        return tag
    }

    String getSource() {
        return source
    }

    String getVisit() {
        return visit
    }

    String getProtocol() {
        return protocol
    }

    String getTimeZone() {
        return timeZone
    }

    String getTimestamp() {
        return timestamp
    }

    String getProject() {
        return project
    }

    String getFolderName() {
        return folderName
    }

    String getStatus() {
        return status
    }

    String getExternalUrl() {
        return externalUrl
    }

    String getName() {
        return name
    }

    Boolean getPreventAnon() {
        return preventAnon
    }

    Boolean getPreventAutoCommit() {
        return preventAutoCommit
    }

    Long getId() {
        return id
    }

    SessionDataTriple getSessionTriple() {
        return sessionTriple
    }
}
