package com.example.information_systems_lab1.authentication.controller;

import com.example.information_systems_lab1.authentication.requsts.AddToAdminQueueRequest;
import com.example.information_systems_lab1.authentication.service.AdminService;
import com.example.information_systems_lab1.authentication.service.UserServices;
import com.example.information_systems_lab1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;


    @PostMapping("/addToQueue")
    public ResponseEntity<?> addAdminToAdminQueue(@RequestBody AddToAdminQueueRequest id) {
        adminService.add(id.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/queue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getQueue() {
        return new ResponseEntity<>(adminService.getAllAdminQueues(), HttpStatus.OK);
    }

    @PostMapping("/set")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setAdmin(@RequestBody AddToAdminQueueRequest id) {
        adminService.setAdmin(id.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectAdmin(@RequestBody AddToAdminQueueRequest id) {
        adminService.reject(id.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
