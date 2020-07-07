package com.rho.challenge.dao;

import com.rho.challenge.model.Notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class NotificationDAOTest {

    @Autowired
    private NotificationDAO notification_dao;

    @Test
    @Transactional
    @Rollback(true)
    public void testSaveNotification(){
        Notification n = new Notification(1,120.5);
        int ret = notification_dao.storeNotification(n);
        assertNotNull(n);
        assertEquals(ret, 1);
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testGetNotification(){
        Notification n = new Notification(1,120.0);
        int ret = notification_dao.storeNotification(n);
        assertNotNull(n);
        assertEquals(ret, 1);

        Notification n1 = notification_dao.getNotification(1);
        assertEquals(n, n1);
    }

}