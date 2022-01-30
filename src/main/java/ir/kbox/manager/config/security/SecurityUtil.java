package ir.kbox.manager.config.security;

import ir.kbox.manager.controller.exceptions.ForbiddenException;
import ir.kbox.manager.controller.exceptions.UnAuthorizedException;
import ir.kbox.manager.model.user.Roles;
import ir.kbox.manager.model.user.User;
import ir.kbox.manager.model.user.UserPrincipal;
import ir.kbox.manager.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final SessionRegistry sessionRegistry;
    private final UserDetailsServiceImpl userDetailsService;

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

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getUser();
        } else {
            return userDetailsService.loadUserByUsername(principal.toString()).getUser();
        }
    }


    public User checkSessionAndGetUser(String sessionId) {
        UserPrincipal userPrincipal = getUserPrincipal(sessionId);
        if(userPrincipal == null){
            throw new UnAuthorizedException("Session Invalid Exception!");
        }
        return userPrincipal.getUser();
    }

    public UserPrincipal getUserPrincipal(String sessionId) {
        SessionInformation sessionInformation = sessionRegistry.getSessionInformation(sessionId);
        if (sessionInformation == null || sessionInformation.isExpired()) {
            return null;
        }

        return (UserPrincipal) sessionInformation.getPrincipal();
    }

}
