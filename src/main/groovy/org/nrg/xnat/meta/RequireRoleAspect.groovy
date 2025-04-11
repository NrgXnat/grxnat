package org.nrg.xnat.meta;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.nrg.xnat.interfaces.XnatInterface;
import org.nrg.xnat.subinterfaces.XnatFunctionalitySubinterface;

@Aspect
class RequireRoleAspect {

    void checkUserRole(XnatInterface xnatInterface, RequireRole requiredRole) throws Throwable {
        if (!xnatInterface.userHasRole(requiredRole.value())) {
            throw new UnsupportedOperationException('You must have role ' + requiredRole.value() + ' to perform this operation.')
        }
    }

    @Before('target(xnatSubinterface) && @within(requiredRole)')
    void checkUserRole(XnatFunctionalitySubinterface xnatSubinterface,  RequireRole requiredRole) throws Throwable {
        checkUserRole(xnatSubinterface.xnatInterface, requiredRole)
    }
}
