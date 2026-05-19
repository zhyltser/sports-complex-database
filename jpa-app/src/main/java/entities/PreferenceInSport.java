package entities;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "preference_in_sport")
public class PreferenceInSport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_preference", nullable = false)
    private Integer id;

    @Column(name = "preference_name", nullable = false, length = 64, unique = true)
    private String preferenceName;

    @ManyToMany(mappedBy = "preferences")
    private Set<Client> clients = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPreferenceName() {
        return preferenceName;
    }

    public void setPreferenceName(String preferenceName) {
        this.preferenceName = preferenceName;
    }

    public Set<Client> getClients() { return clients; }

    public void setClients(Set<Client> clients) { this.clients = clients; }

    public void addClient(Client client) {
        clients.add(client);
    }

    public void removeClient(Client client) {
        clients.remove(client);
    }
}