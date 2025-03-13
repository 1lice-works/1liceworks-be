package com.elice.iliceworksbe.common.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class EmailProperty {
    private String host;
    private int port;
    private String username;
    private String password;
    private Properties properties;

    @Data
    public static class Properties {
        private Smtp smtp;

        @Data
        public static class Smtp {
            private boolean auth;
            private int timeout;
            private Starttls starttls;

            @Data
            public static class Starttls {
                private boolean enable;
            }
        }
    }

}
