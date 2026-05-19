package DAO;

import entities.Training;
import jakarta.persistence.EntityManager;

public class TrainingDAO extends BaseDAO<Training> {
    public TrainingDAO(EntityManager em) {
        super(em, Training.class);
    }
}