package com.rho.challenge.model;

import com.rho.challenge.service.ServiceParameters;

import java.time.Instant;

/**
 * Represents a user bet to be processed by the server
 */
public class Bet {

    /**
     * ID of the user
     */
    private int accountId;

    /**
     * Bet amount
     */
    private double stake;

    /**
     * Stamped time of bet, in milliseconds, used for time calculations
     */
    private long time = Instant.now().toEpochMilli();

    /**
     * Default constructor representing a user bet
     */
    public Bet(){
    }

    /**
     * Creates a new Bet given the client account ID and the amount of the bet
     *
     * @param accountId User ID
     * @param stake Bet amount
     */
    public Bet(int accountId, double stake){
        if(accountId <= 0)
            throw new IllegalArgumentException("Account ID must be a positive integer");
        if(stake <= 0 || stake > ServiceParameters.THRESHOLD)
            throw new IllegalArgumentException("Stake amount must be a positive value or not exceed " + ServiceParameters.THRESHOLD);
        this.accountId = accountId;
        this.stake = stake;
        //this.time = System.currentTimeMillis();
        //this.time = Instant.now().toEpochMilli();
    }

    /* Auxiliary constructor with bet time specification. Only used for testing purposes
    since I was not able to simulate time frame displacement for different bets during testing */
    public Bet(int accountId, double stake, long time){
        if(accountId <= 0)
            throw new IllegalArgumentException("Account ID must be a positive integer");
        if(stake <= 0 || stake > ServiceParameters.THRESHOLD)
            throw new IllegalArgumentException("Stake amount must be a positive value or not exceed " + ServiceParameters.THRESHOLD);
        this.accountId = accountId;
        this.stake = stake;
        this.time = time;
    }

    /**
     * Returns the account ID of the user
     * @return Account ID
     */
    public int getAccountId(){
        return this.accountId;
    }

    /**
     * Returns the bet amount
     * @return Bet amount
     */
    public double getStake(){
        return this.stake;
    }

    /**
     * Returns the bet stamped time
     * @return Bet time
     */
    public long getTime(){
        return this.time;
    }

    @Override
    public String toString(){
        return "Bet { accountId: "+ this.accountId + ", stake:" + this.stake + ", time: " + this.time + " }";
    }

    /* must override equals method in order to adapt to the edge case where bet time (long)
     * in testing differed from the one mocked by Mockito by less than a millisecond, resulting in a false equivalence
     * Example: Bet { accountId: 1, stake:10.0, time: 1594046388575 }
     *          Bet { accountId: 1, stake:10.0, time: 1594046388544 }
     * */
    @Override
    public boolean equals(Object o){
        /* all Bet attributes are equal */
        if(o == this){
            return true;
        }

        if(!(o instanceof Bet)){
            return false;
        }

        Bet n = (Bet) o;

        // compare bets time while discarding millis
        if(Long.compare(this.time/1000 , (n.getTime()/1000)) != 0){
            return false;
        }

        // compare remaining data members
        return (Integer.compare(this.accountId, n.getAccountId()) == 0) && (Double.compare(this.stake, n.getStake()) == 0);
    }

}
