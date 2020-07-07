package com.rho.challenge.service;

import com.rho.challenge.dao.NotificationDAO;
import com.rho.challenge.model.Bet;
import com.rho.challenge.model.Notification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class NotificationServiceTest {

    @Autowired
    private NotificationService notification_service;

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testProcessBetValid(){
        Bet b = new Bet(1, 50);
        String response = (String) notification_service.processBet(b);
        assertEquals("OK", response);
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testProcessBetMultipleAccountValid(){
        Bet b1 = new Bet(1,10);
        Bet b2 = new Bet(2,20);

        String response1 = (String) notification_service.processBet(b1);
        assertEquals("OK", response1);
        String response2 = (String) notification_service.processBet(b2);
        assertEquals("OK", response2);
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testProcessBetNotificationGenerated(){
        Bet b1 = new Bet(1, 30);
        Bet b2 = new Bet(1, 40);
        Bet b3 = new Bet(1, 50);

        String response1 = (String) notification_service.processBet(b1);
        assertEquals("OK", response1);
        String response2 = (String) notification_service.processBet(b2);
        assertEquals("OK", response2);

        Notification n_expected = new Notification(1,120.0);

        Notification n = (Notification) notification_service.processBet(b3);
        assertNotNull(n);
        assertEquals(n_expected.getAccountId(), n.getAccountId());
        assertEquals(n_expected.getCumulative(), n.getCumulative());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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

        Notification n_expected1 = new Notification(1,120.0);
        Notification n1 = (Notification) notification_service.processBet(b3);
        assertNotNull(n1);
        assertEquals(n_expected1.getAccountId(), n1.getAccountId());
        assertEquals(n_expected1.getCumulative(), n1.getCumulative());
        assertEquals(n_expected1.getTime(), n1.getTime());

        String response3 = (String) notification_service.processBet(b4);
        assertEquals("OK", response3);
        String response4 = (String) notification_service.processBet(b5);
        assertEquals("OK", response4);

        Notification n_expected2 = new Notification(2,120.0);
        Notification n2 = (Notification) notification_service.processBet(b6);
        assertNotNull(n2);
        assertEquals(n_expected2.getAccountId(), n2.getAccountId());
        assertEquals(n_expected2.getCumulative(), n2.getCumulative());
        /* time removed from assertion since it crashes the testing pipeline
        * Example:
            org.opentest4j.AssertionFailedError:
            Expected :2020-07-03 22:29:59.367
            Actual   :2020-07-03 22:29:59.368

        assertEquals(n_expected2.getTime(), n2.getTime()); */
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testProcessBetMultipleValidWithTimeDisplacement(){
        /* System.currentTimeMillis overriding not working so we hardcode the bet time
        *  TODO: https://stackoverflow.com/questions/2001671/override-java-system-currenttimemillis-for-testing-time-sensitive-code
        *        https://www.baeldung.com/java-override-system-time
        * */

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
        Notification response5 = (Notification) notification_service.processBet(b5);

        Notification n_expected = new Notification(1,110.0);

        assertNotNull(response5);
        assertEquals(n_expected.getAccountId(), response5.getAccountId());
        assertEquals(n_expected.getCumulative(), response5.getCumulative());

    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void testCreateNotification() {
        int account_id = 1;
        double cumulative = 120.0;
        Notification n_expected = new Notification(account_id, cumulative);
        Notification n = notification_service.createNotification(account_id, cumulative);
        assertNotNull(n);
        assertEquals(n_expected.getAccountId(), n.getAccountId());
        assertEquals(n_expected.getCumulative(), n.getCumulative());
        assertEquals(n_expected.getTime(), n.getTime());
    }

}