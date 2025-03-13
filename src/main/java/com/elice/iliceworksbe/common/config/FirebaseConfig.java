package com.elice.iliceworksbe.common.config;

import com.elice.iliceworksbe.common.config.property.FirebaseProperty;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

    private final FirebaseProperty firebaseProperty;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream(firebaseProperty.getJsonPath());

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);
    }
}
