package com.rho.challenge.repository;

import com.rho.challenge.model.Notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notification_dao;

    @Test
    @Transactional
    @Rollback(true)
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    public void testSaveNotification(){
        Notification n = new Notification(1,120.5);
        Notification ret = notification_dao.save(n);
        assertNotNull(n);
        assertEquals(ret, n);
    }

    @Test
    @Transactional
    @Rollback(true)
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    public void testNotificationStoredCorrectly(){
        Notification n = new Notification(1,120.0);
        Notification ret = notification_dao.save(n);
        assertNotNull(ret);
        assertEquals(ret, n);

        Iterable<Notification> iter =  notification_dao.findAll();

        for (Notification notification : iter) {
            assertEquals(notification.getAccountId(), n.getAccountId());
            assertEquals(notification.getCumulative(), n.getCumulative());
        }

    }

}