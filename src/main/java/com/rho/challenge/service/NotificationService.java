package com.rho.challenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.challenge.exception.CustomException;
import com.rho.challenge.exception.EntityType;
import com.rho.challenge.exception.ExceptionType;
import com.rho.challenge.model.UserData;
import com.rho.challenge.repository.NotificationRepository;
import com.rho.challenge.model.Bet;
import com.rho.challenge.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * NotificationService Class defines the business logic of the server, where it receives
 * players bets, stores them and analyzes their history based on the maximum threshold amount
 * they can bet during a specified bet processing window.
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository repository;

    private Map<Integer, ArrayList<UserData>> clientsManager = new HashMap<>();

    public String processMessage(String message) throws CustomException, JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        Bet bet = mapper.readValue(message, Bet.class);

        String response = processBet(bet);

        System.out.println(response);

        return response;

    }

    public String processBet(Bet bet) throws CustomException{

        System.out.println(bet);

        // check if account has any bet registered
        if(!clientsManager.containsKey(bet.getAccountId())){
            System.out.println("Account ID: " + bet.getAccountId() +" added hashmap");

            /* if current bet stake exceeds the specified threshold amount it cannot be accepted
            and a notification is created */
            if(bet.getStake() >= ServiceParameters.THRESHOLD){
                System.out.println(bet + " discarded. Maximum threshold " + ServiceParameters.THRESHOLD + " exceeded!");
                return createNotification(bet.getAccountId(), bet.getStake()).toJSONString();
            }

            // client bet history data stored as : userId => [ { stake, time }, { stake, time } ... ]
            UserData userData = new UserData(bet.getStake(), bet.getTime());
            ArrayList<UserData> client_bet_history = new ArrayList<>();
            client_bet_history.add(userData);
            clientsManager.put(bet.getAccountId(),client_bet_history);
        }else{
            // check if the account bet cumulative in current_time - TIME_WINDOW

            //long window_start_sec = (System.currentTimeMillis()/1000) - ServiceParameters.TIME_WINDOW_SECONDS;
            long window_start_sec = bet.getTime() - ServiceParameters.TIME_WINDOW_MILLIS;
            // System.out.println(window_start_sec);
            //long window_end = bet.getTime()/1000;

            /* iterates through the account bet history, removing bets no longer required to be processed,
            that is, bets older than TIME_WINDOW_SECONDS, while calculating the cumulative bet amount generated
            in that time span */
            double bet_total_time_window = processAccountBets(bet.getAccountId(), window_start_sec);

            System.out.println("TOTAL bet amount: " + bet_total_time_window);

            double cumulative = bet_total_time_window + bet.getStake();

            /* plus check if the current bet exceeds the specified THRESHOLD and, if so,
            * generate and publish a notification */
            if(cumulative >= ServiceParameters.THRESHOLD) {

                System.out.println(bet + " discarded. Maximum threshold " + ServiceParameters.THRESHOLD + " exceeded!");

                return createNotification(bet.getAccountId(), cumulative).toJSONString();

            }

            /* in case the stake does not exceed the threshold it is added to the account betting history */
            UserData userData = new UserData(bet.getStake(), bet.getTime());
            ArrayList<UserData> client_bet_history = clientsManager.get(bet.getAccountId());
            client_bet_history.add(userData);
            clientsManager.replace(bet.getAccountId(), client_bet_history);
            System.out.println("Updated account " + bet.getAccountId() + " bet history");
        }

        return "OK";

    }

    /*
    Iterates through the bet history of a certain account and discards bets situated outside the
    processing window span ( that is bets outside the range [current_time-60, current_time]) while
    calculating the cumulative amount of the bets inside the window span
     */
    public double processAccountBets(int accountId, long windowStart){

        double stakesCumulative = 0.0;
        ArrayList<UserData> player_bets = clientsManager.get(accountId);

        /* need to use an Iterator in order to dynamically iterate through the array and remove old bets */
        for(Iterator<UserData> iterator = player_bets.iterator(); iterator.hasNext();){

            UserData currentData = iterator.next();
            double currentBet = currentData.getStake();
            long time = currentData.getTime();

            // check if old bet
            if( time <= windowStart){
                System.out.println("Account " + accountId + " Bet registered at " + new Timestamp(time) + " removed!");
                iterator.remove();
            }else{
                stakesCumulative += (double) currentBet;
            }

        }

        return stakesCumulative;

    }

    /* create notification, which will be then stored to the local db */
    public Notification createNotification(int accountId, double cumulative) throws CustomException {

        Notification n = new Notification(accountId, cumulative);

        System.out.println(n);

        try {
            Notification ret = repository.save(n);
            System.out.println("Notification stored successfully");
            return ret;
        }catch (Exception ex){
            System.out.print("Error: Store notification failed. Cause: " + ex.getLocalizedMessage());
            throw CustomException.throwException(EntityType.REPOSITORY, ExceptionType.STORAGE_FAILED_EXCEPTION, ex.getLocalizedMessage());
        }

    }

}
