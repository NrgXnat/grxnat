package org.nrg.xnat.rest

class XnatAliasToken implements XnatAuthProvider {

    String alias
    String secret

    XnatAliasToken(String alias, String secret) {
        this.alias = alias
        this.secret = secret
    }

    XnatAliasToken() {}

    @Override
    XnatSessionFilter createSessionFilter(String xnatUrl, boolean allowInsecureSSL) {
        new AliasTokenSessionFilter(this, xnatUrl, allowInsecureSSL)
    }

}
