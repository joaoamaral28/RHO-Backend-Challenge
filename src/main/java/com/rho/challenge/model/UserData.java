package com.rho.challenge.model;

/**
 * Auxiliary class to represent a user bet and its registered time
 */
public class UserData {

    private double betStake;
    private long time;

    /**
     * Creates the class with the given stake amount and time
     *
     * @param betStake bet amount
     * @param time bet registered time at the server
     */
    public UserData(double betStake, long time){
        this.betStake = betStake;
        this.time = time;
    }

    public double getStake(){
        return this.betStake;
    }

    public long getTime(){
        return this.time;
    }

}
