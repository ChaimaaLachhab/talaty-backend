package com.talaty.controller;

import com.talaty.dto.request.AdminRequestDto;
import com.talaty.dto.response.AdminResponseDto;
import com.talaty.mapper.UserMapper;
import com.talaty.model.Admin;
import com.talaty.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for managing admins.
 */
@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;
    private final UserMapper userMapper; // Declare the mapper

    @Autowired
    public AdminController(AdminService adminService, UserMapper userMapper) {
        this.adminService = adminService;
        this.userMapper = userMapper;
    }

    /**
     * Retrieve all admins.
     *
     * @return List of all admins.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<AdminResponseDto>> getAllAdmins() {
        List<Admin> admins = adminService.getAllAdmins();
        List<AdminResponseDto> adminResponseDtos = admins.stream()
                .map(userMapper::toAdminResponseDto)
                .toList();
        return new ResponseEntity<>(adminResponseDtos, HttpStatus.OK);
    }

    /**
     * Retrieve an admin by ID.
     *
     * @return The admin with the specified ID.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/details")
    public ResponseEntity<AdminResponseDto> getAdminById(@AuthenticationPrincipal Admin currentAdmin) {
        Admin admin = adminService.getAdminById(currentAdmin.getId());
        AdminResponseDto adminResponseDto = userMapper.toAdminResponseDto(admin);
        return admin != null ? new ResponseEntity<>(adminResponseDto, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Update an existing admin.
     *
     * @param adminDTO  The admin with updated information.
     * @return The updated admin.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdminResponseDto> updateAdmin(@RequestPart("admin") AdminRequestDto adminDTO,
                                                        @RequestPart(value = "userPhoto", required = false) MultipartFile userPhoto,
                                                        @AuthenticationPrincipal Admin admin) {
        Admin updatedAdmin = adminService.updateAdmin(adminDTO, admin, userPhoto);
        AdminResponseDto adminResponseDto = userMapper.toAdminResponseDto(updatedAdmin);
        return new ResponseEntity<>(adminResponseDto, HttpStatus.OK);
    }

    /**
     * Delete an admin by ID.
     *
     * @param id The ID of the admin to delete.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
