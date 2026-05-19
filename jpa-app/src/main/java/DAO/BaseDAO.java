package DAO;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

// Abstract base DAO class to provide generic CRUD operations for any entity type T
public abstract class BaseDAO<T> {

    // Logger for logging info, warnings, and errors
    private static final Logger logger = LoggerFactory.getLogger(BaseDAO.class);

    // Entity class type
    private final Class<T> entityClass;

    // EntityManager for interacting with the persistence context
    protected final EntityManager em;

    // Constructor
    public BaseDAO(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    // Find entities by a given field (arg) and its value
    public List<T> findByArg(String arg, Object val) {
        try {
            // Create a dynamic JPQL query based on the field name
            String query = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e." + arg + " = :val";
            List<T> result = em.createQuery(query, entityClass)
                    .setParameter("val", val)
                    .getResultList();
            if (result.isEmpty()) {
                // Warn if result is empty
                logger.warn("Could not find {} with {} = {}", entityClass.getSimpleName(), arg, val);
            } else {
                // Info if result was found
                logger.info("Found {} with {} = {}", entityClass.getSimpleName(), arg, val);
            }
            return result;
        } catch (Exception e) {
            // Error if exception
            logger.error("Error finding {} with {} = {}", entityClass.getSimpleName(), arg, val, e);
            return new ArrayList<>();
        }
    }

    // Retrieve all entities of type T from the database
    public List<T> findAll() {
        try {
            // Dynamic JPQL query to select all records
            List<T> list = em.createQuery("select e from " + entityClass.getSimpleName() + " e", entityClass).getResultList();
            if (list.isEmpty()) {
                // Warn if result is empty
                logger.warn("Table {} is empty", entityClass.getSimpleName());
            } else {
                // Info if result was found
                logger.info("Table {} contains {} records", entityClass.getSimpleName(), list.size());
            }
            return list;
        } catch (Exception e) {
            // Error if exception
            logger.error("Error finding all {}", entityClass.getSimpleName(), e);
            return new ArrayList<>();
        }
    }

    // Persist a new entity in the database
    public void create(T t) {
        try {
            em.persist(t);
            // Info if success
            logger.info("Successfully created entity: {}", entityClass.getSimpleName());
        } catch (Exception e) {
            // Error if exception
            logger.error("Error creating entity: {}", entityClass.getSimpleName(), e);
        }
    }

    // Delete an entity from the database
    public void delete(T t) {
        try {
            // Ensure the entity is managed before removal
            em.remove(em.merge(t));
            // Info if success
            logger.info("Successfully deleted entity: {}", entityClass.getSimpleName());
        } catch (Exception e) {
            // Error if exception
            logger.error("Error deleting entity: {}", entityClass.getSimpleName(), e);
        }
    }

    // Delete entities by a given field and value using JPQL
    public void deleteByArg(String arg, Object val) {
        try {
            String query = "DELETE FROM " + entityClass.getSimpleName() + " e WHERE e." + arg + " = :val";
            int result = em.createQuery(query)
                    .setParameter("val", val)
                    .executeUpdate();

            // Log how many records were deleted
            if (result > 0) {
                // Info if something is found
                logger.info("Deleted {} record(s) from {} where {} = {}", result, entityClass.getSimpleName(), arg, val);
            } else {
                // Warn if is not
                logger.warn("No records found to delete in {} where {} = {}", entityClass.getSimpleName(), arg, val);
            }
        } catch (Exception e) {
            // Error if exception
            logger.error("Error deleting from {} where {} = {}", entityClass.getSimpleName(), arg, val, e);
        }
    }

    // Update an entity using merge (saves the changes to the database)
    public void update(T t) {
        try {
            em.merge(t);
            // Info if success
            logger.info("Successfully updated entity: {}", entityClass.getSimpleName());
        } catch (Exception e) {
            // Error if exception
            logger.error("Error updating entity: {}", entityClass.getSimpleName(), e);
        }
    }

    // Update records where a field has a specific old value to a new value
    public void updateByArg(String arg, Object oldVal, Object newVal) {
        try {
            String query = "UPDATE " + entityClass.getSimpleName() + " e SET e." + arg + " = :newValue WHERE e." + arg + " = :oldValue";
            int updatedCount = em.createQuery(query)
                    .setParameter("oldValue", oldVal)
                    .setParameter("newValue", newVal)
                    .executeUpdate();

            // Log how many records were updated
            if (updatedCount > 0) {
                // Info if something is found
                logger.info("Successfully updated {} record(s) where {} = {}", updatedCount, arg, oldVal);
            } else {
                // Warn if is not
                logger.warn("No records found to update where {} = {}", arg, oldVal);
            }
        } catch (Exception e) {
            // Error if exception
            logger.error("Error updating entity with {} = {}", arg, oldVal, e);
        }
    }
}
