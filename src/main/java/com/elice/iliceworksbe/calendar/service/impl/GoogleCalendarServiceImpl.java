package com.elice.iliceworksbe.calendar.service.impl;

import com.elice.iliceworksbe.calendar.config.property.GoogleCalendarProperty;
import com.elice.iliceworksbe.calendar.entity.Event;
import com.elice.iliceworksbe.calendar.repository.CalendarRepository;
import com.elice.iliceworksbe.calendar.repository.EventRepository;
import com.elice.iliceworksbe.calendar.service.GoogleCalendarService;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarRequestInitializer;
import com.google.api.services.calendar.model.Events;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    private final GoogleCalendarProperty googleCalendarProperty;
    private final EventRepository eventRepository;

    private final static JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final CalendarRepository calendarRepository;


    /**
     * Google calendar API를 사용하기 위한 Calendar 서비스를 생성해서 반환하는 함수
     *
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private Calendar createGoogleCalendarService() throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        return new Calendar.Builder(httpTransport, JSON_FACTORY, null)
                .setCalendarRequestInitializer(new CalendarRequestInitializer(googleCalendarProperty.getApiKey()))
                .build();
    }

    /**
     * 2020년부터 2030년까지의
     * 공휴일 데이터를 가져오는 API
     *
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    @Override
    public void insertKoreaHolidayFromGoogleCalendar() throws IOException, GeneralSecurityException {
        Calendar service = createGoogleCalendarService();


        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2030, 12, 31);

        // LocalDate → Date 변환 (Google API는 java.util.Date 사용)
        Date timeMin = Date.from(startDate.atStartOfDay(ZoneId.of("UTC")).toInstant());
        Date timeMax = Date.from(endDate.atTime(23, 59, 59).atZone(ZoneId.of("UTC")).toInstant());

        Events events = service.events().list(googleCalendarProperty.getGoogleKoreaHolidayId())
                .setTimeMin(new DateTime(timeMin))
                .setTimeMax(new DateTime(timeMax))
                .setMaxResults(200)
                .execute();

        com.elice.iliceworksbe.calendar.entity.Calendar otherCalendar = calendarRepository.findFirstByTypeId(-1L);

        List<Event> eventList = events.getItems().stream().map(e -> Event.of(e, otherCalendar)).toList();

        eventRepository.saveAll(eventList);

        log.info("2020년-2030년까지의 일정들 데이터 삽입완료 events count : {}", eventList.size());
    }
}
