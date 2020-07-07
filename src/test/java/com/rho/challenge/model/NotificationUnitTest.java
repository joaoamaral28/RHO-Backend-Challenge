package com.rho.challenge.model;

import com.rho.challenge.service.ServiceParameters;
import org.hibernate.cache.internal.NoCachingTransactionSynchronizationImpl;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class NotificationUnitTest {

    @Test
    void testCumulativeValueOufOfBounds(){

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Notification(1, 90.0);
        });
        String expected_msg = "Cumulative value must exceed " + ServiceParameters.THRESHOLD + " value";
        String actual_msg = exception.getMessage();
        assertTrue(actual_msg.contains(expected_msg));

        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            new Notification(1, -40.93);
        });
        String expected_msg1 = "Cumulative value must exceed " + ServiceParameters.THRESHOLD + " value";
        String actual_msg1 = exception1.getMessage();
        assertTrue(actual_msg1.contains(expected_msg1));

    }

    @Test
    void testIllegalAccountId(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Notification(0, 120.0);
        });
        String expected_msg = "Account ID must be a positive integer";
        String actual_msg = exception.getMessage();
        assertTrue(actual_msg.contains(expected_msg));

    }

    @Test
    void testNotificationEquals(){
        Notification n1 = new Notification(1,120.0);
        Notification n2 = new Notification(1,120.0);

        assertTrue(n1.equals(n2));
    }

    @Test
    void testNotificationNotEqualsByTimestamp(){

        String t1 = "2020-07-03T10:00:00Z";
        Clock clock1 = Clock.fixed(Instant.parse(t1), ZoneId.of("UTC"));
        String t2 = "2020-07-03T10:00:01Z";
        Clock clock2 = Clock.fixed(Instant.parse(t2), ZoneId.of("UTC"));

        Notification n1 = new Notification(1,120.0, Timestamp.from(Instant.now(clock1)));
        Notification n2 = new Notification(1,120.0, Timestamp.from(Instant.now(clock2)));

        assertFalse(n1.equals(n2));
    }

}