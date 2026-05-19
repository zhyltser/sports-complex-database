package DAO;

import entities.Hspecialization;
import jakarta.persistence.EntityManager;

public class HspecializationDAO extends BaseDAO<Hspecialization> {
    public HspecializationDAO(EntityManager em) {
        super(em, Hspecialization.class);
    }
}
