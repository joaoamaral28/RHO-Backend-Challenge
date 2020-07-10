package com.rho.challenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rho.challenge.exception.CustomException;
import com.rho.challenge.model.Bet;
import com.rho.challenge.model.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class NotificationServiceTest {

    @Autowired
    private NotificationService notification_service;

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testProcessMessageOK(){

        String msg = "{\"accountId\":\"1\",\"stake\":\"10\"}";
        String msg1 = "{\"accountId\":1,\"stake\":20}";

        String response = "";
        String response1 = "";

        try {
            response = notification_service.processMessage(msg);
            response1 = notification_service.processMessage(msg1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals("OK", response);
        assertEquals("OK", response1);
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testProcessMessageMultipleAccountValidMessages(){
        String msg = "{\"accountId\":\"1\",\"stake\":\"10\"}";
        String msg1 = "{\"accountId\":2,\"stake\":20}";
        String msg2 = "{\"accountId\":\"3\",\"stake\":\"89.5\"}";
        String msg3 = "{\"accountId\":4,\"stake\":55.55}";

        String response = "";
        String response1 = "";
        String response2 = "";
        String response3 = "";

        try {
            response = notification_service.processMessage(msg);
            response1 = notification_service.processMessage(msg1);
            response2 = notification_service.processMessage(msg2);
            response3 = notification_service.processMessage(msg3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals("OK", response);
        assertEquals("OK", response1);
        assertEquals("OK", response2);
        assertEquals("OK", response3);

    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testProcessMessageInvalidField(){

        String msg = "{\"account_id\":\"1\",\"stake\":\"10\"}";
        String msg1 = "{\"accountId\":\"1\",\"stake\":}";
        String msg2 = "";

        Exception ex1 = assertThrows(JsonProcessingException.class, () -> {
            notification_service.processMessage(msg);
        });
        Exception ex2 = assertThrows(JsonProcessingException.class, () -> {
            notification_service.processMessage(msg1);
        });
        Exception ex3 = assertThrows(JsonProcessingException.class, () -> {
            notification_service.processMessage(msg2);
        });

    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testProcessMessageReturnNotification() throws CustomException, JsonProcessingException {
        String msg = "{\"accountId\":\"1\",\"stake\":\"10\"}";
        String msg1 = "{\"accountId\":\"1\",\"stake\": 99}";

        Notification n = new Notification(1,109.0);

        String ack = notification_service.processMessage(msg);
        String strNotification = notification_service.processMessage(msg1);

        assertEquals("OK", ack);
        assertEquals(n.toJSONString(), strNotification);
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testProcessBetValid() throws CustomException {
        Bet b = new Bet(1, 50);
        String response = notification_service.processBet(b);
        assertEquals("OK", response);
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testProcessBetMultipleAccountValid() throws CustomException {
        Bet b1 = new Bet(1,10);
        Bet b2 = new Bet(2,20);

        String response1 = (String) notification_service.processBet(b1);
        assertEquals("OK", response1);
        String response2 = (String) notification_service.processBet(b2);
        assertEquals("OK", response2);
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testProcessBetMultipleValid() throws CustomException {
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
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testProcessBetNotificationGenerated() throws CustomException {
        Bet b1 = new Bet(1, 30);
        Bet b2 = new Bet(1, 40);
        Bet b3 = new Bet(1, 50);

        String response1 = (String) notification_service.processBet(b1);
        assertEquals("OK", response1);
        String response2 = (String) notification_service.processBet(b2);
        assertEquals("OK", response2);

        Notification n_expected = new Notification(1,120.0);
        String strNotification = notification_service.processBet(b3);
        assertEquals(n_expected.toJSONString(), strNotification);

    }


    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testProcessBetNotificationGeneratedMultipleAccount() throws CustomException {
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

        String strNotification1 = notification_service.processBet(b3);
        assertEquals(n_expected1.toJSONString(), strNotification1);

        String strNotification2 = notification_service.processBet(b6);

        assertEquals(n_expected2.toJSONString(), strNotification2);

    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testProcessBetMultipleValidWithTimeDisplacement() throws CustomException {

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
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testProcessBetMultipleValidWithTimeDisplacementAndNotification() throws CustomException {
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

        String strNotification = notification_service.processBet(b5);

        assertEquals(n_expected.toJSONString(), strNotification);

    }

    @Test
    void testCreateNotification() throws CustomException {
        int account_id = 1;
        double cumulative = 120.0;
        Notification n_expected = new Notification(account_id, cumulative);
        Notification n = notification_service.createNotification(account_id, cumulative);
        assertNotNull(n);
        assertEquals(n_expected, n);
    }

}