import entities.Client;
import entities.Reservation;
import jakarta.persistence.*;
import queries.Query;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();

        Query query = new Query(em);

        // 1. Insert
        String login = "johnn";
        String password = "sndfjsdjfsjfd";
        // query.insertTrainer(login, password);

        // 2. Update
        String newLogin = "smithhh";
        // query.updateTrLogin(login, newLogin);

        // 3. Delete
        // query.deleteTrainer(newLogin);

        // 4. Select (inheritance)
         query.selectClUser(200);

        // 5. Select (Many to Many)
        // query.selectSpHallsSpecs();

        // Transaction
        //Reservation res = query.findUnpaidReservation();
        // Client c = query.findRandomClient();
        // Wrong payment method
        // query.payForReservation(res, c, "crd");
        // Correct
        // query.payForReservation(res, c, "card");
        // Already paid
       //  query.payForReservation(res, c, "card");

        emf.close();
        em.close();
    }
}
