package com.pfe.smsworkflow.Init;


import com.pfe.smsworkflow.Models.ERole;
import com.pfe.smsworkflow.Models.Role;
import com.pfe.smsworkflow.Models.SuperAdmin;
import com.pfe.smsworkflow.Repository.RoleRepository;
import com.pfe.smsworkflow.Repository.SuperadminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.util.Set;


@Component
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final SuperadminRepository superadminRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           SuperadminRepository superadminRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.superadminRepository = superadminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        initRoles();
        initSuperAdmin();
    }

    private void initRoles() {
        createRoleIfNotExists(ERole.ROLE_SUPERADMIN);
        createRoleIfNotExists(ERole.ROLE_ADMIN);
        createRoleIfNotExists(ERole.ROLE_CANDIDAT);
    }

    private void createRoleIfNotExists(ERole role) {
        roleRepository.findByName(role).orElseGet(() -> {
            Role newRole = new Role(role);
            roleRepository.save(newRole);
            System.out.println(role + " créé.");
            return newRole;
        });
    }

    private void initSuperAdmin() {
        if (superadminRepository.findByEmail("admin@gmail.com").isEmpty()) {
            SuperAdmin superAdmin = new SuperAdmin();
            superAdmin.setPhone("22722397");
            superAdmin.setFullName("admin");
            superAdmin.setEmail("admin@gmail.com");
            superAdmin.setIsConfirmMobile(1);
            superAdmin.setPassword(passwordEncoder.encode("123456"));

            Role superAdminRole = roleRepository.findByName(ERole.ROLE_SUPERADMIN)
                    .orElseThrow(() -> new RuntimeException("ROLE_SUPERADMIN introuvable!"));

            superAdmin.setRoles(Set.of(superAdminRole));
            superadminRepository.save(superAdmin);
            System.out.println("Compte administrateur créé : admin@gmail.com|22722397 / 123456");
        } else {
            System.out.println("Le compte administrateur existe déjà.");
        }
    }
}

