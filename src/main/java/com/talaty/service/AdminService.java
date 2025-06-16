package com.talaty.service;

import com.talaty.dto.request.AdminRequestDto;
import com.talaty.mapper.UserMapper;
import com.talaty.model.Admin;
import com.talaty.model.Media;
import com.talaty.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final MediaService mediaService;
    private final UserMapper userMapper;

    @Autowired
    public AdminService(AdminRepository adminRepository, MediaService mediaService, UserMapper userMapper) {
        this.adminRepository = adminRepository;
        this.mediaService = mediaService;
        this.userMapper = userMapper;
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Admin getAdminById(Long id) {
        return adminRepository.findById(id).orElse(null);
    }

    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }

    public Admin updateAdmin(AdminRequestDto adminDTO, Admin admin, MultipartFile userPhoto) {
        Admin updatedAdmin = userMapper.partialUpdateAdmin(adminDTO, admin);

        if (userPhoto != null && !userPhoto.isEmpty()) {
            Media media = mediaService.updateMediaForUser(userPhoto, updatedAdmin);
            updatedAdmin.setUserPhoto(media);
        }

        return adminRepository.save(updatedAdmin);
    }
}
