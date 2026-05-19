package DAO;

import entities.Client;
import jakarta.persistence.EntityManager;

public class ClientDAO extends BaseDAO<Client> {
    public ClientDAO(EntityManager em) {
        super(em, Client.class);
    }
}
