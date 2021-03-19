package org.nrg.xnat.meta

import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.subinterfaces.XnatFunctionalitySubinterface

@Aspect
class RequireAdminAspect {

    @Before('target(xnatInterface) && @annotation(RequireAdmin)')
    void checkAdmin(XnatInterface xnatInterface) throws Throwable {
        if (!xnatInterface.userIsAdmin()) {
            throw new UnsupportedOperationException('You must be an admin to perform this operation.')
        }
    }

    @Before('target(xnatSubinterface) && @annotation(RequireAdmin)')
    void checkAdmin(XnatFunctionalitySubinterface xnatSubinterface) throws Throwable {
        checkAdmin(xnatSubinterface.xnatInterface)
    }

}
