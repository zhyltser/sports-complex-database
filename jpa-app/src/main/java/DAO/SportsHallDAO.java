package DAO;

import entities.SportsHall;
import jakarta.persistence.EntityManager;

public class SportsHallDAO extends BaseDAO<SportsHall> {
    public SportsHallDAO(EntityManager em) {
        super(em, SportsHall.class);
    }
}
