package com.rho.challenge.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rho.challenge.exception.CustomException;
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
    void testProcessBetReturnsOK() throws CustomException, JsonProcessingException {

        String s_bet = "{\"account_id\":\"1\",\"stake\":\"10\"}";
        String s_bet1 = "{\"account_id\":1,\"stake\":20}";

        Mockito.when(notification_service.processMessage(s_bet)).thenReturn("OK");
        String response = notification_controller.processBet(s_bet);

        assertEquals("OK", response);

        Mockito.when(notification_service.processMessage(s_bet1)).thenReturn("OK");
        String response1 = notification_controller.processBet(s_bet1);
        assertEquals("OK", response1);

    }

    @Test
    void testProcessBetReturnsNotification() throws CustomException, JsonProcessingException {

        String s_bet = "{\"account_id\":\"1\",\"stake\":\"100\"}";

        Notification n_expect = new Notification(1,100.0);

        Mockito.when(notification_service.processMessage(s_bet)).thenReturn(n_expect.toJSONString());

        String response = notification_controller.processBet(s_bet);
        String s_expect = n_expect.toJSONString();
        assertNotNull(n_expect);
        assertEquals(s_expect, response);
    }


    @Test
    void testProcessBetBadMessageFormat() throws CustomException, JsonProcessingException {
        String s_bet = "{\"account_id\":\"1\",\"stake\":}";
        String s_bet1 = "{\"accountId\":\"1\" \"stake\":\"2\"}";

        String errorMessage = "{\"message\":\"Invalid JSON request\"}";

        Mockito.when(notification_service.processMessage(s_bet)).thenReturn(errorMessage);
        String response = notification_controller.processBet(s_bet);
        assertEquals(errorMessage, response);

        Mockito.when(notification_service.processMessage(s_bet1)).thenReturn(errorMessage);
        String response1 = notification_controller.processBet(s_bet1);
        assertEquals(errorMessage, response1);

    }

}