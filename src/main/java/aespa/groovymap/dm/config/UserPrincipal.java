package aespa.groovymap.dm.config;

import java.security.Principal;
import lombok.Getter;

@Getter
public class UserPrincipal implements Principal {
    private final Long id;

    public UserPrincipal(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return id.toString();
    }

}