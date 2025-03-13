package com.elice.iliceworksbe.common.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "firebase.storage")
public class FirebaseProperty {
    private String bucketName;
    private String jsonPath;
}
