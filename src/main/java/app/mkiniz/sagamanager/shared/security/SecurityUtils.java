package app.mkiniz.sagamanager.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Objects;
import java.util.Optional;

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<String> getCurrentUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication)
                .map(auth -> {
                    String userName = null;
                    if (auth.getPrincipal() instanceof UserDetails userDetails) {
                        userName =  userDetails.getUsername();
                    } else if (auth.getPrincipal() instanceof Jwt jwt) {
                        userName = jwt.getClaimAsString("preferred_username");
                    } else if (auth.getPrincipal() instanceof String principal) {
                        userName = principal;
                    }
                    return Objects.isNull(userName) ? auth.getName() : userName;
                });
    }
}
