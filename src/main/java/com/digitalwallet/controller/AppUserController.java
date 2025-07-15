package com.digitalwallet.controller;

import com.digitalwallet.entity.AppUser;

import com.digitalwallet.service.AppUserService;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/createUser")

public class AppUserController {
    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }
    @PostMapping
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    public ResponseEntity<AppUser> createAppUser(@RequestBody AppUser appUser) {
        AppUser created = appUserService.createAppUser(appUser);
        return ResponseEntity.ok(created);
    }

}
