package com.google.cloud.healthcare.fdamystudies.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_details_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserDetailsBO user;

    @Column(name = "institution_id")
    private String institutionId;
}
