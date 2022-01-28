package ir.kbox.manager.config.security;

import ir.kbox.manager.controller.exceptions.ForbiddenException;
import ir.kbox.manager.controller.exceptions.UnAuthorizedException;
import ir.kbox.manager.model.Roles;
import ir.kbox.manager.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final SessionRegistry sessionRegistry;

    public void checkRoleAdmin(String id) {
        checkRole(Roles.ADMIN, id);
    }

    public void checkRole(Roles role, String sessionId) {
        UserPrincipal user = getUserPrincipal(sessionId);
        if (null == user) {
            throw new UnAuthorizedException("Invalid SessionID");
        }
        if (!user.getAuthorities().contains(role)) {
            throw new ForbiddenException();
        }
    }

    public UserPrincipal getUserPrincipal(String sessionId) {
        SessionInformation sessionInformation = sessionRegistry.getSessionInformation(sessionId);
        if (sessionInformation == null) {
            return null;
        }
        return (UserPrincipal) sessionInformation.getPrincipal();
    }

}
