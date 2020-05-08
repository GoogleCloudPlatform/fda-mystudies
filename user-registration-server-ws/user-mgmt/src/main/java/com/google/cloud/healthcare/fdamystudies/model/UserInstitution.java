package com.google.cloud.healthcare.fdamystudies.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "user_institution")
public class UserInstitution {
    @Id
    @Column(name = "user_institution_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userInstitutionId;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    private UserDetailsBO user;

    @Column(name = "institution_id")
    private String institutionId;
}
