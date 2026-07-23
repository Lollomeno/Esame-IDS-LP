package it.unicam.hackhub.utils.adapters;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import it.unicam.hackhub.model.dto.requestdto.CallBookingResult;
import it.unicam.hackhub.utils.facade.CalendarEventSpec;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;

@Component // <-- Spring lo rileverà e lo fornirà al SupportRequestService
public class GoogleCalendarAdapter implements ICalendarAdapter {

    private static final String APPLICATION_NAME = "Hackathon Manager";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Override
    public CallBookingResult createMeetEvent(String accessToken, String calendarId, CalendarEventSpec spec) {
        // ... Il tuo codice originale per le API di Google rimane INVARIATO ...
        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            AccessToken authAccessToken = new AccessToken(accessToken, null);
            GoogleCredentials credentials = GoogleCredentials.create(authAccessToken);
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

            Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, requestInitializer)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Costruzione dell'evento (lasciato esattamente come lo hai scritto tu)
            // ...
            
            return CallBookingResult.ok("simulated_id", "simulated_link"); // Sostituisci con i veri return del tuo try/catch

        } catch (Exception e) {
            e.printStackTrace();
            return CallBookingResult.fail("Errore durante la creazione dell'evento su Google Calendar: " + e.getMessage());
        }
    }
}