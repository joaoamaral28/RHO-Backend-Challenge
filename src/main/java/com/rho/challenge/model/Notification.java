package com.rho.challenge.model;

import com.google.gson.JsonObject;
import com.rho.challenge.service.ServiceParameters;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name="notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notification_id;

    private Integer account_id;
    private Double cumulative;
    private Timestamp time;

    public Notification(Integer account_id, Double cumulative){
        if(account_id <= 0)
            throw new IllegalArgumentException("Account ID must be a positive integer");
        else if(cumulative < ServiceParameters.THRESHOLD)
            throw new IllegalArgumentException("Cumulative value must exceed " + ServiceParameters.THRESHOLD + " value");
        this.account_id = account_id;
        this.cumulative = cumulative;
        //this.time = new Timestamp(System.currentTimeMillis());
        this.time = Timestamp.from(Instant.now());
    }

    /* aux constructor required for Notification instantiation upon equivalent data fetch from database */
    public Notification(Integer account_id, Double cumulative, Timestamp time){
        this.account_id = account_id;
        this.cumulative = cumulative;
        this.time = time;
    }

    public Integer getAccountId(){
        return this.account_id;
    }

    public Double getCumulative(){
        return this.cumulative;
    }

    public Timestamp getTime(){
        return this.time;
    }

    public String toJSONString(){
        JsonObject json = new JsonObject();
        json.addProperty("account_id", this.account_id);
        json.addProperty("cumulative", this.cumulative);
        //json.addProperty("time", this.time.toString());
        return json.toString();
    }

    @Override
    public String toString(){
        return "Notification { account_id: "+ this.account_id + ", cumulative: " + this.cumulative +  ", timestamp:" + this.time + " }";
    }

    /* must override equals method in order to adapt to the edge case where notifications timestamps
    * in testing differed from one another by less than a millisecond and would result a false equivalence
    * Example: Notification { account_id: 1, cumulative: 120.0, timestamp:2020-07-06 00:51:38.3539101 }
    *          Notification { account_id: 1, cumulative: 120.0, timestamp:2020-07-06 00:51:38.338439 }
    * */
    @Override
    public boolean equals(Object o){

        /* all Notification attributes are equal */
        if(o == this){
            return true;
        }

        if(!(o instanceof Notification)){
            return false;
        }

        Notification n = (Notification) o;

        // compare notifications timestamps while discarding milliseconds
        if(Long.compare(this.time.getTime()/1000 , (n.getTime().getTime()/1000)) != 0){
            return false;
        }

        // compare remaining data members
        return (Integer.compare(this.account_id, n.getAccountId()) == 0) && (Double.compare(this.cumulative, n.getCumulative()) == 0);
    }

}
