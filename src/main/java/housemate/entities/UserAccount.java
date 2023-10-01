/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import housemate.constants.Role;
import jakarta.persistence.*;
import lombok.*;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "UserAccount")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role = Role.CUSTOMER;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number", nullable = true, unique = true)
    private String phoneNumber;

    @JsonIgnore
    @Column(name = "password_hash", nullable = true)
    private String passwordHash;

    @Column(name = "email_address", unique = true)
    private String emailAddress;

    @Column(name = "email_validation_status")
    private boolean emailValidationStatus;

    @Column(name = "avatar")
    private String avatar;

    public UserAccount(String fullName, String emailAddress, boolean emailValidationStatus) {
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.emailValidationStatus = emailValidationStatus;
    }
}
