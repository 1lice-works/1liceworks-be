package com.elice.iliceworksbe.auth.model;

import com.elice.iliceworksbe.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getAccountId();
    }

    // UserDetail의 username은 accountId이다. (로그인 시 ID가 accountId가 됨.)
    // 이 메소드의 username은 사용자명을 말한다.
    public String getName(){
        return user.getUsername();
    }

    public Long getUserId(){
        return user.getId();
    }
}
