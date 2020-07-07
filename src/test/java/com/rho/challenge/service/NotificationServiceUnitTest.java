package com.rho.challenge.service;

import com.rho.challenge.dao.NotificationDAO;
import com.rho.challenge.model.Bet;
import com.rho.challenge.model.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceUnitTest {

    @InjectMocks
    private NotificationService notification_service;

    @Mock
    private NotificationDAO notification_dao;

    @Test
    void testProcessBetValid(){
        Bet b = new Bet(1, 50);
        String response = (String) notification_service.processBet(b);
        assertEquals("OK", response);
    }

    @Test
    void testProcessBetMultipleAccountValid(){
        Bet b1 = new Bet(1,10);
        Bet b2 = new Bet(2,20);

        String response1 = (String) notification_service.processBet(b1);
        assertEquals("OK", response1);
        String response2 = (String) notification_service.processBet(b2);
        assertEquals("OK", response2);
    }

    @Test
    void testProcessBetMultipleValid(){
        Bet b1 = new Bet(1, 10);
        Bet b2 = new Bet(1, 20.2);
        Bet b3 = new Bet(1, 30);
        Bet b4 = new Bet(1, 35.56);

        String response1 = (String) notification_service.processBet(b1);
        assertEquals("OK", response1);
        String response2 = (String) notification_service.processBet(b2);
        assertEquals("OK", response2);
        String response3 = (String) notification_service.processBet(b3);
        assertEquals("OK", response3);
        String response4 = (String) notification_service.processBet(b4);
        assertEquals("OK", response4);
    }


    @Test
    void testProcessBetNotificationGenerated(){
        Bet b1 = new Bet(1, 30);
        Bet b2 = new Bet(1, 40);
        Bet b3 = new Bet(1, 50);

        String response1 = (String) notification_service.processBet(b1);
        assertEquals("OK", response1);
        String response2 = (String) notification_service.processBet(b2);
        assertEquals("OK", response2);

        Notification n_expected = new Notification(1,120.0);

        Mockito.when(notification_dao.storeNotification(n_expected)).thenReturn(1);
        Notification n = (Notification) notification_service.processBet(b3);
        assertEquals(n_expected, n);
        // Mockito.verify(notification_dao, Mockito.times(1)).storeNotification((n_expected));
    }


    @Test
    void testProcessBetNotificationGeneratedMultipleAccount(){
        Bet b1 = new Bet(1, 30);
        Bet b2 = new Bet(1, 40);
        Bet b3 = new Bet(1, 50);
        Bet b4 = new Bet(2, 30);
        Bet b5 = new Bet(2, 40);
        Bet b6 = new Bet(2, 50);

        String response1 = (String) notification_service.processBet(b1);
        assertEquals("OK", response1);
        String response2 = (String) notification_service.processBet(b2);
        assertEquals("OK", response2);
        String response3 = (String) notification_service.processBet(b4);
        assertEquals("OK", response3);
        String response4 = (String) notification_service.processBet(b5);
        assertEquals("OK", response4);

        Notification n_expected1 = new Notification(1,120.0);
        Notification n_expected2 = new Notification(2,120.0);

        Mockito.when(notification_dao.storeNotification(n_expected1)).thenReturn(1);
        Notification n1 = (Notification) notification_service.processBet(b3);
        assertEquals(n_expected1, n1);

        Mockito.when(notification_dao.storeNotification(n_expected2)).thenReturn(1);
        Notification n2 = (Notification) notification_service.processBet(b6);

        assertEquals(n_expected2, n2);

    }

    @Test
    void testProcessBetMultipleValidWithTimeDisplacement(){

        long time1 = 1593766800000L; // 2020-07-03 10:00:00
        long time2 = 1593766830000L; // 2020-07-03 10:00:30
        long time3 = 1593766850000L; // 2020-07-03 10:00:50
        long time4 = 1593766880000L; // 2020-07-03 10:01:20

        Bet b1 = new Bet(1, 30, time1); // valid bet
        Bet b2 = new Bet(1, 40, time2); // valid bet
        Bet b3 = new Bet(1, 10, time3); // valid bet
        Bet b4 = new Bet(1, 30, time4); // valid bet, since b1 outside time window

        String response1 = (String) notification_service.processBet(b1);
        assertEquals("OK", response1);
        String response2 = (String) notification_service.processBet(b2);
        assertEquals("OK", response2);
        String response3 = (String) notification_service.processBet(b3);
        assertEquals("OK", response3);
        String response4 = (String) notification_service.processBet(b4);
        assertEquals("OK", response4);

    }

    @Test
    void testProcessBetMultipleValidWithTimeDisplacementAndNotification(){
        long time1 = 1593766800000L; // 2020-07-03 10:00:00
        long time2 = 1593766830000L; // 2020-07-03 10:00:30
        long time3 = 1593766850000L; // 2020-07-03 10:00:50
        long time4 = 1593766880000L; // 2020-07-03 10:01:20
        long time5 = 1593766885000L; // 2020-07-03 10:01:25

        Bet b1 = new Bet(1, 30, time1); // valid bet
        Bet b2 = new Bet(1, 40, time2); // valid bet
        Bet b3 = new Bet(1, 10, time3); // valid bet
        Bet b4 = new Bet(1, 30, time4); // valid bet, since b1 outside time window
        Bet b5 = new Bet(1,30, time5);  // invalid bet, trigger notification

        String response1 = (String) notification_service.processBet(b1);
        assertEquals("OK", response1);
        String response2 = (String) notification_service.processBet(b2);
        assertEquals("OK", response2);
        String response3 = (String) notification_service.processBet(b3);
        assertEquals("OK", response3);
        String response4 = (String) notification_service.processBet(b4);
        assertEquals("OK", response4);

        Notification n_expected = new Notification(1,110.0);

        Mockito.when(notification_dao.storeNotification(n_expected)).thenReturn(1);
        Notification response5 = (Notification) notification_service.processBet(b5);

        assertEquals(n_expected, response5);

    }



    @Test
    void testCreateNotification() {
        int account_id = 1;
        double cumulative = 120.0;
        Notification n_expected = new Notification(account_id, cumulative);

        Mockito.when(notification_dao.storeNotification(n_expected)).thenReturn(1);

        Notification n = notification_service.createNotification(account_id, cumulative);
        assertNotNull(n);
        assertEquals(n_expected, n);
    }

}