package org.e2e.labe2e03.user.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.e2e.labe2e03.coordinate.domain.Coordinate;

import java.time.ZonedDateTime;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "double precision default 0.0")
    private Double avgRating;

    @Column(nullable = false)
    private Role role;

    @Column(columnDefinition = "double precision default 0.0")
    private Integer trips;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    private ZonedDateTime updatedAt;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    @ManyToOne
    @JoinColumn
    private Coordinate coordinate;
}