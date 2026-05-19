package DAO;

import entities.Payment;
import jakarta.persistence.EntityManager;

public class PaymentDAO extends BaseDAO<Payment> {
    public PaymentDAO(EntityManager em) {
        super(em, Payment.class);
    }
}
