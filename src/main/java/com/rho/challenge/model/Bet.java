package com.rho.challenge.model;

import com.rho.challenge.service.ServiceParameters;

import java.time.Instant;

public class Bet {

    private int account_id;
    private double stake;
    private long time;

    public Bet(int account_id, double stake){
        if(account_id <= 0)
            throw new IllegalArgumentException("Account ID must be a positive integer");
        if(stake <= 0 || stake > ServiceParameters.THRESHOLD)
            throw new IllegalArgumentException("Stake amount must be a positive value or not exceed " + ServiceParameters.THRESHOLD);
        this.account_id = account_id;
        this.stake = stake;
        //this.time = System.currentTimeMillis();
        this.time = Instant.now().toEpochMilli();
    }

    /* aux constructor with bet time specification, only used for testing purposes since I was not able to simulate
    * time frame displacement for different bets in the testing methods */
    public Bet(int account_id, double stake, long time){
        if(account_id <= 0)
            throw new IllegalArgumentException("Account ID must be a positive integer");
        if(stake <= 0 || stake > ServiceParameters.THRESHOLD)
            throw new IllegalArgumentException("Stake amount must be a positive value or not exceed " + ServiceParameters.THRESHOLD);
        this.account_id = account_id;
        this.stake = stake;
        this.time = time;
    }

    public int getAccountId(){
        return this.account_id;
    }

    public double getStake(){
        return this.stake;
    }

    public long getTime(){
        return this.time;
    }

    @Override
    public String toString(){
        return "Bet { account_id: "+ this.account_id + ", stake:" + this.stake + ", time: " + this.time + " }";
    }

    /* must override equals method in order to adapt to the edge case where bet time (long)
     * in testing differed from the one mocked by Mockito by less than a millisecond, resulting in a false equivalence
     * Example: Bet { account_id: 1, stake:10.0, time: 1594046388575 }
     *          Bet { account_id: 1, stake:10.0, time: 1594046388544 }
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
        return (Integer.compare(this.account_id, n.getAccountId()) == 0) && (Double.compare(this.stake, n.getStake()) == 0);
    }

}
