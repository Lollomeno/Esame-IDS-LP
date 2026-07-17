package it.unicam.hackhub.model;

import it.unicam.hackhub.model.valueobjs.Username;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Username username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(name = "team_id")
    private Long team;

    // Il costruttore converte automaticamente la stringa nel Value Object per garantirti la validazione
    public User(String username, String name, String surname) {
        this.username = new Username(username); 
        this.name = name;
        this.surname = surname;
    }

    public void assignTeam(Long teamId) {
        this.team = teamId;
    }
}