package com.elice.iliceworksbe.auth.filter;

import com.elice.iliceworksbe.auth.dto.LoginRequestDTO;
import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.auth.utils.JwtTokenProvider;
import com.elice.iliceworksbe.auth.utils.RefreshTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/*
로그인 시 토큰을 발급 & 리프레시 토큰 설정해주는 필터
 */
@Slf4j(topic = "로그인 시 토큰을 발급 & 리프레시 토큰 설정")
public class JwtLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    public JwtLoginAuthenticationFilter(JwtTokenProvider jwtTokenProvider, RefreshTokenProvider refreshTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenProvider = refreshTokenProvider;
        setFilterProcessesUrl("/api/auth/login"); // 로그인 경로 설정
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("JwtLoginAuthenticationFilter 필터 - attemptAuthentication");
        try {
            // 클라이언트에서 전송한 사용자 정보를 객체로 변환
            LoginRequestDTO loginRequestDTO = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDTO.class);

            // AuthenticationManager가 이메일과 비밀번호를 검증할 수 있도록 검증용 토큰을 만듦.
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.accountId(),
                            loginRequestDTO.password()
                    );

            // AuthenticationManager로 인증 시도
            return getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        log.info("JwtLoginAuthenticationFilter 필터 - successfulAuthentication");

        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();

        // 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails.getUsername(), userDetails.getUserId());
        String refreshToken = refreshTokenProvider.createAndStoreRefreshToken(userDetails.getUserId());

        // 리프레시 토큰을 쿠키로 설정
        refreshTokenProvider.setRefreshTokenCookie(response, refreshToken);

        // 액세스 토큰 JWT를 클라이언트에 반환
        response.setContentType("application/json");
        response.getWriter().write("{\"accessToken\": \"" + accessToken + "\"}");
        response.getWriter().flush();
    }

}
