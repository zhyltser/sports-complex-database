package queries;

import DAO.*;
import entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Query {
    private final EntityManager em;

    // DAO instances for entities
    TrainerDAO trainerDAO;
    ClientDAO clientDAO;
    SportsHallDAO sportsHallDAO;
    ReservationDAO reservationDAO;
    PaymentDAO paymentDAO;

    private static final Logger logger = LoggerFactory.getLogger(Query.class);

    // Constructor
    public Query(EntityManager em) {
        this.em = em;
        trainerDAO = new TrainerDAO(em);
        clientDAO = new ClientDAO(em);
        sportsHallDAO = new SportsHallDAO(em);
        reservationDAO = new ReservationDAO(em);
        paymentDAO = new PaymentDAO(em);
    }

    // Creates and inserts a new trainer with the given login and password
    public void insertTrainer(String login, String password) {
        em.getTransaction().begin();
        Trainer t = new Trainer();

        t.setLogin(login);
        byte[] byte_password = password.getBytes(StandardCharsets.UTF_8);
        t.setPassword(byte_password);

        trainerDAO.create(t);
        em.getTransaction().commit();
    }

    // Updates the login of a trainer from oldLogin to newLogin
    public void updateTrLogin(String oldLogin, String newLogin) {
        em.getTransaction().begin();
        trainerDAO.updateByArg("login", oldLogin, newLogin);
        em.getTransaction().commit();
    }

    // Deletes a trainer by login
    public void deleteTrainer(String login) {
        em.getTransaction().begin();
        trainerDAO.deleteByArg("login", login);
        em.getTransaction().commit();
    }

    // Displays login, password and preferences of a client with the given ID
    public void selectClUser(int id) {
        Client c = clientDAO.findByArg("id", id).getFirst();
        System.out.println("From User:\n - login: " + c.getLogin() + "\n - password: " + c.getPassword());
        System.out.println("From Client preferences:");
        Set<PreferenceInSport> preferences = c.getPreferences();
        for (PreferenceInSport preference : preferences) {
            System.out.println(" - " + preference.getPreferenceName());
        }
    }

    // Displays all sports halls and their associated specializations
    public void selectSpHallsSpecs() {
        int i = 1;
        List<SportsHall> halls = sportsHallDAO.findAll();
        for (SportsHall sh : halls) {
            System.out.printf("%2d. name: %s%n", i, sh.getHallName());
            System.out.println("    specializations:");
            for (Hspecialization hspec : sh.getSpecializations()) {
                System.out.println("    - " + hspec.getSpecializationName());
            }
            i++;
        }
    }

    // Finds the first reservation with status "reserved"
    public Reservation findUnpaidReservation() {
        return reservationDAO.findByArg("status", "reserved").getFirst();
    }

    // Selects a random client from the database
    public Client findRandomClient() {
        List<Client> clients = clientDAO.findAll();
        if (clients.isEmpty()) return null;

        Random random = new Random();
        return clients.get(random.nextInt(clients.size()));
    }

    // Processes payment for a reservation using a specific client and payment method
    public void payForReservation(Reservation reservation, Client client, String paymentMethod) {
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();

            // Check if reservation is still unpaid
            if (!"reserved".equals(reservation.getStatus())) {
                logger.error("Reservation with id = " + reservation.getId() + " has already been paid!");
                trans.rollback();
                return;
            }

            // Validate payment method
            if (!"card".equals(paymentMethod) && !"cash".equals(paymentMethod)) {
                logger.error("Wrong payment method: {}", paymentMethod);
                trans.rollback();
                return;
            }

            // Create a new payment and associate it with the reservation
            BigDecimal price = reservation.getIdTraining().getPriceForOne();
            Payment payment = new Payment();
            payment.setPaymentMethod(paymentMethod);
            payment.setFinalPrice(price);
            payment.addReservation(reservation);
            payment.setPaymentDate(Instant.now().minus(Duration.ofMinutes(5))); // simulate delay

            // Link payment to client and reservation
            client.addPayment(payment);
            reservation.setIdPayment(payment);
            reservation.setStatus("payed");

            // Persist changes
            paymentDAO.create(payment);
            clientDAO.update(client);
            reservationDAO.update(reservation);

            trans.commit();
            logger.info("Payment {} successful for reservation {}", payment.getId(), reservation.getId());

        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            logger.error("Error during payment for reservation " + reservation.getId(), e);
        }
    }
}
