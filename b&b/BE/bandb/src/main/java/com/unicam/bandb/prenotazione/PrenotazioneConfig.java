package com.unicam.bandb.prenotazione;

import com.unicam.bandb.authJwt.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrenotazioneConfig {

    @Bean
    CommandLineRunner commandLineRunner(AuthenticationService service){
            return args-> {/*
                var admin = RegisterRequest.builder()
                        .email("admin@mail.com")
                        .password("password")
                        .role(ADMIN)
                        .build();
                System.out.println("ADMIN token: "+service.register(admin).getToken());*/
            };

        }

}
