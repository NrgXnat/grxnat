package org.nrg.xnat.meta

import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.nrg.xnat.interfaces.XnatInterface

@Aspect
class RequireAdminAspect {

    @Before('@annotation(RequireAdmin) && target(xnatInterface)')
    void checkAdmin(XnatInterface xnatInterface) throws Throwable {
        if (!xnatInterface.userIsAdmin()) {
            throw new UnsupportedOperationException('You must be an admin to perform this operation.')
        }
    }

}
