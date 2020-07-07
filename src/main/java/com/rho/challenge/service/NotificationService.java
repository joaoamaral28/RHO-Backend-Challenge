package com.rho.challenge.service;

import com.rho.challenge.dao.NotificationDAO;
import com.rho.challenge.model.Bet;
import com.rho.challenge.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class NotificationService {

    private NotificationDAO notification_dao;

    private Map<Integer, ArrayList<Object>> clients_manager = new HashMap<>();

    @Autowired
    public NotificationService(@Qualifier("testdb") NotificationDAO notification_dao ){
        this.notification_dao = notification_dao;
    }

    public Object processBet(Bet bet){

        System.out.println(bet);

        // check if account has any bet registered
        if(!clients_manager.containsKey(bet.getAccountId())){
            System.out.println("Account ID: " + bet.getAccountId() +" added hashmap");

            /* if current bet stake exceeds the specified threshold amount it cannot be accepted
            and a notification is created */
            if(bet.getStake() >= ServiceParameters.THRESHOLD){
                System.out.println(bet + " discarded. Maximum threshold " + ServiceParameters.THRESHOLD + " exceeded!");
                return createNotification(bet.getAccountId(), bet.getStake());
            }

            // client bet history data stored as : ID => [ { stake, time }, { stake, time } ... ]
            Object tmp[] = { bet.getStake(), bet.getTime()};
            ArrayList<Object> client_bet_history = new ArrayList<>();
            client_bet_history.add(tmp);
            clients_manager.put(bet.getAccountId(),client_bet_history);

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

                return createNotification(bet.getAccountId(), cumulative);

                /* Test if notification was correctly stored
                Notification n1 = notification_dao.getNotification(bet.getAccountId());
                if(n1 != null){
                    System.out.println("Notification retrieved successfully from database");
                }else{
                    System.out.print("Failed to retrieve notification from database");
                }
                System.out.println(n1);
                */

            }

            /* in case the stake does not exceed it is added to the account betting history */

            Object tmp[] = { bet.getStake(), bet.getTime() };
            ArrayList<Object> client_bet_history = clients_manager.get(bet.getAccountId());
            client_bet_history.add(tmp);
            clients_manager.replace(bet.getAccountId(), client_bet_history);

            System.out.println("Updated account " + bet.getAccountId() + " bet history");
        }

        return "OK";
    }

    /*
    Iterates through the bet history of a certain account and discards bets situated outside the
    processing window span ( that is bets outside the range [current_time-60, current_time]) while
    calculating the cumulative amount of the bets inside the window span
     */
    public double processAccountBets(int account_id, long window_start){

        double stakes_cumulative = 0.0;
        ArrayList<Object> player_bets = clients_manager.get(account_id);

        for(Iterator<Object> iterator = player_bets.iterator(); iterator.hasNext();){
            Object[] current_bet = (Object[]) iterator.next();

            //System.out.println( (double) current_bet[0] );
            //System.out.println( (long) current_bet[1] );

            // check if old bet
            if( (long) current_bet[1] <= window_start){
                System.out.println("Account " + account_id + " Bet " + (long) current_bet[1] + " removed!");
                iterator.remove();
            }else{
                stakes_cumulative += (double) current_bet[0];
            }

        }

        return stakes_cumulative;

    }

    /* create notification, which will be then stored to the local db */
    public Notification createNotification(int account_id, double cumulative){

        Notification n = new Notification(account_id, cumulative);

        System.out.println(n);

        int ret = notification_dao.storeNotification(n);
        if(ret == 1){
            System.out.println("Notification stored successfully");
        }else{
            System.out.print("Failed to store notification");
            return null;
        }

        return n;

    }

}
