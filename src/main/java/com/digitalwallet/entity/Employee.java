package com.digitalwallet.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    private String name;

    @OneToOne
    @JoinColumn(name = "app_user_id", nullable = false)
    @JsonBackReference
    @Schema(hidden = true)
    private AppUser appUser;


    public Employee() {
    }

    public Employee(Long id, String name, AppUser appUser) {
        this.id = id;
        this.name = name;
        this.appUser = appUser;
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AppUser getAppUser() {
        return appUser;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
}
