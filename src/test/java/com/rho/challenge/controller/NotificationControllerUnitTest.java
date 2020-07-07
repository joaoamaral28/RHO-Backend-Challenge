package com.rho.challenge.controller;

import com.rho.challenge.model.Bet;
import com.rho.challenge.model.Notification;
import com.rho.challenge.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/*
   A controller unit test only allows to test, in isolation, if the underlying business logic
   is working as intended. The full test of the Controller is performed in the Integration test
   where input validation, input deserialization, output serialization and exception translation
   are correctly evaluated.
   https://reflectoring.io/spring-boot-web-controller-test/
 */

@ExtendWith(MockitoExtension.class)
class NotificationControllerUnitTest {

    @InjectMocks
    private NotificationController notification_controller;

    @Mock
    private NotificationService notification_service;

    @Test
    void testProcessBetReturnsOK() {

        String s_bet = "{\"account_id\":\"1\",\"stake\":\"10\"}";
        String s_bet1 = "{\"account_id\":1,\"stake\":20}";

        Bet b1 = new Bet(1,10);
        Bet b2 = new Bet(1,20);

        Mockito.when(notification_service.processBet(b1)).thenReturn("OK");
        String response = notification_controller.processBet(s_bet);
        Mockito.when(notification_service.processBet(b2)).thenReturn("OK");
        String response1 = notification_controller.processBet(s_bet1);

        assertEquals("OK", response);
        assertEquals("OK", response1);

    }

    @Test
    void testProcessBetReturnsNotification(){
        String s_bet = "{\"account_id\":\"1\",\"stake\":\"100\"}";
        Bet b1 = new Bet(1,100);
        Notification n_expect = new Notification(1,100.0);
        Mockito.when(notification_service.processBet(b1)).thenReturn(n_expect);
        String response = notification_controller.processBet(s_bet);
        String s_expect = n_expect.toJSONString();
        assertNotNull(n_expect);
        assertEquals(s_expect, response);
    }

    @Test
    void testProcessBetBadMessageFormat(){
        String s_bet = "{\"account_id\":\"1\",\"stake\":}";
        String s_bet1 = "{\"account_id\":\"1\" \"stake\":\"2\"}";

        //Mockito.when(notification_service.processBet(b1)).thenReturn("OK");
        String response = notification_controller.processBet(s_bet);
        //Mockito.when(notification_service.processBet(b2)).thenReturn("OK");
        String response1 = notification_controller.processBet(s_bet1);

        assertNotNull(response);
        assertEquals("Invalid JSON format", response);
        assertNotNull(response1);
        assertEquals("Invalid JSON format", response1);

    }

    @Test
    void testProcessBetBadMessageElement(){
        String s_bet = "{\"account_id\":\"1\",\"stake\":\"abc\"}";
        String s_bet1 = "{\"account_id\":\"@@\", \"stake\":\"2\"}";
        String response = notification_controller.processBet(s_bet);
        String response1 = notification_controller.processBet(s_bet1);

        assertNotNull(response);
        assertEquals("Bet ID and Stake amount must be digits", response);
        assertNotNull(response1);
        assertEquals("Bet ID and Stake amount must be digits", response1);
    }

}