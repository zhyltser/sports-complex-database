package entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hspecialization")
public class Hspecialization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_specialization", nullable = false)
    private Integer id;

    @Column(name = "specialization_name", nullable = false, length = 64, unique = true)
    private String specializationName;

    @ManyToMany(mappedBy = "specializations")
    private Set<SportsHall> sportsHalls = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSpecializationName() {
        return specializationName;
    }

    public void setSpecializationName(String specializationName) {
        this.specializationName = specializationName;
    }

    public Set<SportsHall> getSportsHalls() { return sportsHalls; }

    public void setSportsHalls(Set<SportsHall> sportsHalls) { this.sportsHalls = sportsHalls; }

    public void addSportsHall(SportsHall sportsHall) {
        this.sportsHalls.add(sportsHall);
    }

    public void removeSportsHall(SportsHall sportsHall) {
        this.sportsHalls.remove(sportsHall);
    }
}
