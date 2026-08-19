package com.example.backend_facely;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.backend_facely.enums.Role;
import com.example.backend_facely.service.UtilisateurService;

@SpringBootApplication
public class BackendFacelyApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendFacelyApplication.class, args);
    }

    @Bean
    CommandLineRunner createAdmin(UtilisateurService utilisateurService) {
        return args -> {
            try {
                utilisateurService.createInternal(
                    "FACELY",
                    "DIAWARA",
                    "facely@diawara.com",
                    "diawara@626",
                    Role.ADMIN
                );

                System.out.println("=================================");
                System.out.println("ADMIN CRÉÉ AVEC SUCCÈS");
                System.out.println("Email : facely@diawara.com");
                System.out.println("Mot de passe : diawara626");
                System.out.println("Role : ADMIN");
                System.out.println("=================================");

            } catch (Exception e) {
                System.out.println("Admin déjà existant ou erreur : " + e.getMessage());
            }
        };
    }
}
