package org.nrg.xnat.rest

class XnatAliasToken {

    String alias
    String secret

    XnatAliasToken(String alias, String secret) {
        this.alias = alias
        this.secret = secret
    }

    XnatAliasToken() {}

}
