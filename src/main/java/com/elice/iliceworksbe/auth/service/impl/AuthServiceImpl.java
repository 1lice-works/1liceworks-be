package com.elice.iliceworksbe.auth.service.impl;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.auth.service.AuthService;
import com.elice.iliceworksbe.common.constant.Role;
import com.elice.iliceworksbe.common.constant.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        User user = userRepository.findByAccountId(accountId).orElseThrow(() -> new UsernameNotFoundException(accountId));
        return new UserDetailsImpl(user);
    }

    @PostConstruct
    public void init(){
        User user = User.builder()
                .accountId("hi563@threadly.ilice-works.com")
                .username("정태승")
                .password(passwordEncoder.encode("!a12345678"))
                .privateEmail("hi563@naver.com")
                .role(Role.LEADER)
                .status(Status.ACTIVE)
                .isTeamCreated(false)
                .build();

        userRepository.save(user);
    }
}
