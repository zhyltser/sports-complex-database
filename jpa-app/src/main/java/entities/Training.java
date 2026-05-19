package entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "training",
        uniqueConstraints = @UniqueConstraint(columnNames = {"training_date", "id_sports_hall"}))
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_training", nullable = false)
    private Integer id;

    @Column(name = "training_date", nullable = false)
    private Instant trainingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "id_sports_hall")
    private SportsHall idSportsHall;

    @Column(name = "type", nullable = false, length = 32)
    private String type;

    @Column(name = "capacity", nullable = false)
    private Short capacity;

    @Column(name = "price_for_one", nullable = false)
    private BigDecimal priceForOne;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "conduction",
            joinColumns = @JoinColumn(name = "id_training"),
            inverseJoinColumns = @JoinColumn(name = "id_trainer")
    )
    private Set<Trainer> trainers = new HashSet<>();

    @OneToMany(mappedBy = "idTraining", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reservation> reservations = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instant getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(Instant trainingDate) {
        this.trainingDate = trainingDate;
    }

    public SportsHall getIdSportsHall() {
        return idSportsHall;
    }

    public void setIdSportsHall(SportsHall idSportsHall) {
        this.idSportsHall = idSportsHall;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Short getCapacity() {
        return capacity;
    }

    public void setCapacity(Short capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getPriceForOne() {
        return priceForOne;
    }

    public void setPriceForOne(BigDecimal priceForOne) {
        this.priceForOne = priceForOne;
    }

    public Set<Trainer> getTrainers() { return trainers; }

    public void setTrainers(Set<Trainer> trainers) { this.trainers = trainers; }

    public void addTrainer(Trainer trainer) {
        trainers.add(trainer);
    }

    public void removeTrainer(Trainer trainer) {
        trainers.remove(trainer);
    }

    public Set<Reservation> getReservations() { return reservations; }

    public void setReservations(Set<Reservation> reservations) { this.reservations = reservations; }
}
