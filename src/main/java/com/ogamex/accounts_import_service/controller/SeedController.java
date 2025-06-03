package com.ogamex.accounts_import_service.controller;

import com.ogamex.accounts_import_service.dto.EmailDTO;
import com.ogamex.accounts_import_service.service.SeedService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/seed")
public class SeedController {

    private final SeedService seedService;

    @Autowired
    public SeedController(SeedService seedService) {
        this.seedService = seedService;
    }

    @PostMapping
    public ResponseEntity<Map<String,String>> seedAccount(@RequestBody @Valid EmailDTO payload) {
        try {
            seedService.seedByEmail(payload.getEmail());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }

        // Instead of just “ok”, return a richer English message:
        return ResponseEntity.ok(Map.of(
                "message", "Planet successfully enriched with random fleets, resources, defenses, and buildings"
        ));
    }
}