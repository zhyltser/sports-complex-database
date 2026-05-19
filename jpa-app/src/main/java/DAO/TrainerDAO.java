package DAO;

import entities.Trainer;
import jakarta.persistence.EntityManager;

public class TrainerDAO extends BaseDAO<Trainer> {
    public TrainerDAO(EntityManager em) {
        super(em, Trainer.class);
    }
}
