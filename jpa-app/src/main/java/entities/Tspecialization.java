package entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tspecialization")
public class Tspecialization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_specialization", nullable = false)
    private Integer id;

    @Column(name = "specialization_name", nullable = false, length = 64, unique = true)
    private String specializationName;

    @ManyToMany(mappedBy = "specializations")
    private Set<Trainer> trainers = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) { this.id = id; }

    public String getSpecializationName() {
        return specializationName;
    }

    public void setSpecializationName(String specializationName) {
        this.specializationName = specializationName;
    }

    public Set<Trainer> getTrainers() { return trainers; }

    public void setTrainers(Set<Trainer> trainers){ this.trainers = trainers; }

    public void addTrainer(Trainer trainer){
        trainers.add(trainer);
    }

    public void removeTrainer(Trainer trainer){
        trainers.remove(trainer);
    }
}
