package entities;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sports_hall")
public class SportsHall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sports_hall", nullable = false)
    private Integer id;

    @Column(name = "hall_name", nullable = false, length = 64, unique = true)
    private String hallName;

    @ColumnDefault("1")
    @Column(name = "size", nullable = false)
    private Short size;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "hall_specialization",
            joinColumns = @JoinColumn(name = "id_sports_hall"),
            inverseJoinColumns = @JoinColumn(name = "id_specialization")
    )
    private Set<Hspecialization> specializations = new HashSet<>();

    @OneToMany(mappedBy = "idSportsHall", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Training> trainings = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public Short getSize() {
        return size;
    }

    public void setSize(Short size) {
        this.size = size;
    }

    public Set<Hspecialization> getSpecializations() { return specializations; }

    public void setSpecializations(Set<Hspecialization> specializations) { this.specializations = specializations; }

    public void addSpecialization(Hspecialization specialization) {
        specializations.add(specialization);
        specialization.addSportsHall(this);
    }

    public void removeSpecialization(Hspecialization specialization) {
        specializations.remove(specialization);
        specialization.removeSportsHall(this);
    }

    public Set<Training> getTrainings() { return trainings; }

    public void setTrainings(Set<Training> trainings) { this.trainings = trainings; }

    public void addTraining(Training training) {
        trainings.add(training);
        training.setIdSportsHall(this);
    }

    public void removeTraining(Training training) {
        trainings.remove(training);
        training.setIdSportsHall(null);
    }
}
