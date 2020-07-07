package com.rho.challenge.model;

import com.rho.challenge.service.ServiceParameters;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class BetUnitTest {

    @Test
    void testStakeValueOufOfBounds(){

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Bet(1, 120.0);
        });
        String expected_msg = "Stake amount must be a positive value or not exceed " + ServiceParameters.THRESHOLD;
        String actual_msg = exception.getMessage();
        assertTrue(actual_msg.contains(expected_msg));

        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            new Bet(1, -40);
        });
        String expected_msg1 = "Stake amount must be a positive value or not exceed " + ServiceParameters.THRESHOLD;
        String actual_msg1 = exception1.getMessage();
        assertTrue(actual_msg1.contains(expected_msg1));

    }

    @Test
    void testIllegalAccountId(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Bet(0, 50);
        });
        String expected_msg = "Account ID must be a positive integer";
        String actual_msg = exception.getMessage();
        assertTrue(actual_msg.contains(expected_msg));

    }

    @Test
    void testBetNotEqualsByTime(){

        long time1 = 1593766800000L; // 2020-07-03 10:00:00
        long time2 = 1593766801000L; // 2020-07-03 10:00:01

        Bet n1 = new Bet(1,30, time1);
        Bet n2 = new Bet(1,40.5, time2);

        assertFalse(n1.equals(n2));
    }


}