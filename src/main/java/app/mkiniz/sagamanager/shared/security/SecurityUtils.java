package app.mkiniz.sagamanager.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<String> getCurrentUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication)
                .map(auth -> {
                    if (auth.getPrincipal() instanceof UserDetails userDetails) {
                        return userDetails.getUsername();
                    } else if (auth.getPrincipal() instanceof String principal) {
                        return principal;
                    }
                    return auth.getName();
                });
    }
}
