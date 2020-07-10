package com.rho.challenge.service;

/**
 * The business logic configurable parameters
 */
public class ServiceParameters {

    public static final int THRESHOLD = 100; // Maximum combined bet threshold an account can produce in TIME_SECOND_WINDOWS
    public static final int TIME_WINDOW_SECONDS = 60; // Window duration in seconds in which the clients bets will be considered for processing
    public static final int TIME_WINDOW_MILLIS = TIME_WINDOW_SECONDS * 1000;

}
