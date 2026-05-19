package DAO;

import entities.PreferenceInSport;
import jakarta.persistence.EntityManager;

public class PreferenceInSportDAO extends BaseDAO<PreferenceInSport> {
    public PreferenceInSportDAO(EntityManager em) {
        super(em, PreferenceInSport.class);
    }
}