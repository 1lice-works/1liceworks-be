package com.elice.iliceworksbe.auth.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "token")
public class TokenProperty {
    private String secret;
    private Long accessTokenExpiration;
    private Long refreshTokenExpiration;
}
