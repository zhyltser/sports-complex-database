package DAO;

import entities.User;
import jakarta.persistence.EntityManager;

public class UserDAO extends BaseDAO<User> {
    public UserDAO(EntityManager em) {
        super(em, User.class);
    }
}
