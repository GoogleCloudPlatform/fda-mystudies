package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ur_admin_user")
public class UserRegAdminUser implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "email", columnDefinition = "VARCHAR(100)")
  private String email = "";

  @Column(name = "ur_admin_auth_id", columnDefinition = "VARCHAR(255)")
  private String urAdminAuthId = "";

  @Column(name = "first_name", columnDefinition = "VARCHAR(100)")
  private String firstName = "";

  @Column(name = "last_name", columnDefinition = "VARCHAR(100)")
  private String lastName = "";

  @Column(name = "phone_number", columnDefinition = "VARCHAR(20)")
  private String phoneNumber = "";

  @Column(name = "email_changed", columnDefinition = "TINYINT(1)")
  private Integer emailChanged = 0;

  @Column(name = "status", columnDefinition = "TINYINT(1)")
  private Integer status = 0;

  @Column(name = "manage_users", columnDefinition = "TINYINT(1)")
  private Integer manageUsers = 0;

  @Column(name = "manage_locations", columnDefinition = "TINYINT(1)")
  private Integer manageLocations = 0;

  @Column(name = "created", columnDefinition = "TIMESTAMP")
  private LocalDateTime created;

  @Column(name = "created_by", columnDefinition = "INT(20) default 0")
  private Integer createdBy;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUrAdminAuthId() {
    return urAdminAuthId;
  }

  public void setUrAdminAuthId(String urAdminAuthId) {
    this.urAdminAuthId = urAdminAuthId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public Integer getEmailChanged() {
    return emailChanged;
  }

  public void setEmailChanged(Integer emailChanged) {
    this.emailChanged = emailChanged;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getManageUsers() {
    return manageUsers;
  }

  public void setManageUsers(Integer manageUsers) {
    this.manageUsers = manageUsers;
  }

  public Integer getManageLocations() {
    return manageLocations;
  }

  public void setManageLocations(Integer manageLocations) {
    this.manageLocations = manageLocations;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public Integer getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Integer createdBy) {
    this.createdBy = createdBy;
  }
}
