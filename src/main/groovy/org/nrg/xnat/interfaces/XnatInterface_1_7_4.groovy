package org.nrg.xnat.interfaces

class XnatInterface_1_7_4 extends XnatInterface_1_8_0 {

    @Override
    String readXnatCsrfToken() {
        final String csrfTokenLine = queryBase().get(formatXnatUrl('/app/Index.vm')).then().assertThat().statusCode(200).and().extract().response().asString().split('\n').find { it.contains('var csrfToken') }
        csrfTokenLine.substring(csrfTokenLine.indexOf("'") + 1, csrfTokenLine.lastIndexOf("'"))
    }

}
