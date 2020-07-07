package com.rho.challenge.dao;

import com.rho.challenge.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("testdb")
public class NotificationDataAccessService implements NotificationDAO {

    private final JdbcTemplate jdbcTemplate;
    public NotificationDataAccessService(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Notification getNotification(Integer account_id) {
        Notification n;
        try {
            n = (Notification) jdbcTemplate.queryForObject("SELECT account_id, cumulative, time FROM notifications WHERE account_id=?", new Object[]{account_id}, new NotificationRowMapper());
        }catch(EmptyResultDataAccessException e){
            return null;
        }
        return n;
    }

    @Override
    public int storeNotification(Notification notification) {
        return jdbcTemplate.update("INSERT INTO notifications (account_id, cumulative, time ) VALUES (?,?,?)", notification.getAccountId(), notification.getCumulative(), notification.getTime());
    }
}
