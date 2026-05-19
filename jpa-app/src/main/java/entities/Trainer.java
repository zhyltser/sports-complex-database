package entities;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainer")
public class Trainer extends User {
    @ColumnDefault("false")
    @Column(name = "education", nullable = false)
    private Boolean education = false;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "trainer_specialization",
            joinColumns = @JoinColumn(name = "id_trainer"),
            inverseJoinColumns = @JoinColumn(name = "id_specialization")
    )
    private Set<Tspecialization> specializations = new HashSet<>();

    @ManyToMany(mappedBy = "trainers")
    private Set<Training> trainings = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "trainer_assistant",
            joinColumns = @JoinColumn(name = "id_trainer"),
            inverseJoinColumns = @JoinColumn(name = "id_assistant")
    )
    private Set<Trainer> assistants = new HashSet<>();

    @ManyToMany(mappedBy = "assistants")
    private Set<Trainer> trainers = new HashSet<>();

    public Boolean getEducation() { return education; }

    public void setEducation(Boolean education) {
        this.education = education;
    }

    public Set<Tspecialization> getSpecializations() { return specializations; }

    public void setSpecializations(Set<Tspecialization> specializations) { this.specializations = specializations; }

    public void addSpecialization(Tspecialization specialization) {
        specializations.add(specialization);
        specialization.addTrainer(this);
    }

    public void removeSpecialization(Tspecialization specialization) {
        specializations.remove(specialization);
        specialization.removeTrainer(this);
    }

    public Set<Training> getTrainings() { return trainings; }

    public void setTrainings(Set<Training> trainings) { this.trainings = trainings; }

    public void addTraining(Training training) {
        trainings.add(training);
        training.addTrainer(this);
    }

    public void removeTraining(Training training) {
        trainings.remove(training);
        training.removeTrainer(this);
    }

    public Set<Trainer> getAssistants() { return assistants; }

    public void setAssistants(Set<Trainer> assistants) { this.assistants = assistants; }

    public void addAssistant(Trainer assistant) {
        assistants.add(assistant);
        assistant.addTrainer(this);
    }

    public void removeAssistant(Trainer assistant) {
        assistants.remove(assistant);
        assistant.removeTrainer(this);
    }

    public Set<Trainer> getTrainers() { return trainers; }

    public void setTrainers(Set<Trainer> trainers) { this.trainers = trainers; }

    public void addTrainer(Trainer trainer) {
        trainers.add(trainer);
    }

    public void removeTrainer(Trainer trainer) {
        trainers.remove(trainer);
    }
}
