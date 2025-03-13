package com.elice.iliceworksbe.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService  extends UserDetailsService {
    UserDetails loadUserByUsername(String username);
}
