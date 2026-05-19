package DAO;

import entities.Reservation;
import jakarta.persistence.EntityManager;

public class ReservationDAO extends BaseDAO<Reservation> {
    public ReservationDAO(EntityManager em) {
        super(em, Reservation.class);
    }
}
