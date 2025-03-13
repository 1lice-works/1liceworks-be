package com.elice.iliceworksbe.auth.web;

import com.elice.iliceworksbe.auth.dto.LoginRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Operation(summary = "일반 이메일 로그인 요청 (AT X)", description = "일반 이메일 로그인을 합니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO signInRequestDto) {
        // Swagger 용. 실제 구현은 Filter에 존재
        return ResponseEntity.ok().build();
    }

}
