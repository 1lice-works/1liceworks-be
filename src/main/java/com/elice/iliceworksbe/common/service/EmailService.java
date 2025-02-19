package com.elice.iliceworksbe.common.service;

public interface EmailService {
    void sendEmail(String to, String subject, String content);
}
