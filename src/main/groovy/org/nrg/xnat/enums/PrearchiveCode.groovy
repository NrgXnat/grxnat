package org.nrg.xnat.enums;

enum PrearchiveCode {
    MANUAL                  (0),
    AUTO_ARCHIVE            (4),
    AUTO_ARCHIVE_OVERWRITE  (5)

    private final int code

    PrearchiveCode(int code) {
        this.code = code
    }

    public int getCode() {
        return code
    }
}
