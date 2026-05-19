package entities;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "client")
public class Client extends User {
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "client_preference",
            joinColumns = @JoinColumn(name = "id_client"),
            inverseJoinColumns = @JoinColumn(name = "id_preference")
    )
    private Set<PreferenceInSport> preferences = new HashSet<>();

    @OneToMany(mappedBy = "idClient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reservation> reservations = new HashSet<>();

    @OneToMany(mappedBy = "idClient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Payment> payments = new HashSet<>();

    public Set<PreferenceInSport> getPreferences() { return preferences; }

    public void setPreferences(Set<PreferenceInSport> preferences) { this.preferences = preferences; }

    public void addPreference(PreferenceInSport preference) {
        preferences.add(preference);
        preference.addClient(this);
    }

    public void removePreference(PreferenceInSport preference) {
        preferences.remove(preference);
        preference.removeClient(this);
    }

    public Set<Payment> getPayments() { return payments; }

    public void setPayments(Set<Payment> payments) { this.payments = payments; }

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setIdClient(this);
    }

    public void removePayment(Payment payment) {
        payments.remove(payment);
        payment.setIdClient(null);
    }

    public Set<Reservation> getReservations() { return reservations; }

    public void setReservations(Set<Reservation> reservations) { this.reservations = reservations; }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setIdClient(this);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
        reservation.setIdClient(null);
    }
}
