package com.digitalwallet.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    private String name;

    private String surname;

    @Column(unique = true, nullable = false, length = 11)
    private String TCKN;

    @OneToOne
    @JoinColumn(name = "app_user_id", nullable = false)
    @JsonBackReference
    @Schema(hidden = true)
    private AppUser appUser;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Wallet> wallets;


    public Customer() {
    }

    public Customer(Long id, String name, String surname, String TCKN, AppUser appUser, List<Wallet> wallets) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.TCKN = TCKN;
        this.appUser = appUser;
        this.wallets = wallets;
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getTCKN() {
        return TCKN;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public List<Wallet> getWallets() {
        return wallets;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setTCKN(String TCKN) {
        this.TCKN = TCKN;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public void setWallets(List<Wallet> wallets) {
        this.wallets = wallets;
    }
}
