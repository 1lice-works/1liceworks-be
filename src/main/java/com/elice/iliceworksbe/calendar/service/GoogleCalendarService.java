package com.elice.iliceworksbe.calendar.service;

import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleCalendarService {
    void insertKoreaHolidayFromGoogleCalendar() throws IOException, GeneralSecurityException;
}
