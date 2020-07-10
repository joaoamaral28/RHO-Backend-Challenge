package com.rho.challenge.repository;

import com.rho.challenge.model.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Represents the servers DAO (Data Access Object). By extending CrudRepository, and since
 * only regular database queries are needed, there is no need to explicitly implement any
 * methods and queries since spring automatically handles it for us.
 */
@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
}
