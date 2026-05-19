package DAO;

import entities.Tspecialization;
import jakarta.persistence.EntityManager;

public class TspecializationDAO extends BaseDAO<Tspecialization> {
    public TspecializationDAO(EntityManager em) {
        super(em, Tspecialization.class);
    }
}