package DAO;

import entities.Address;
import entities.PersonalData;
import jakarta.persistence.EntityManager;

public class PersonalDataDAO extends BaseDAO<PersonalData> {
    public PersonalDataDAO(EntityManager em) {
        super(em, PersonalData.class);
    }
}
