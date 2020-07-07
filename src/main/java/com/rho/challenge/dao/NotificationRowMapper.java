package com.rho.challenge.dao;

import com.rho.challenge.model.Notification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/* Custom RowMapper class to ease the mapping of database table fields to Notification class type*/
public class NotificationRowMapper implements RowMapper<Notification> {

    @Override
    public Notification mapRow(ResultSet rs, int row_number) throws SQLException {
        return new Notification(rs.getInt("account_id"),
                rs.getDouble("cumulative"),
                rs.getTimestamp("time"));
    }

}

