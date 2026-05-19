package DAO;

import entities.Address;
import jakarta.persistence.EntityManager;

public class AddressDAO extends BaseDAO<Address> {
    public AddressDAO(EntityManager em) {
        super(em, Address.class);
    }
}
