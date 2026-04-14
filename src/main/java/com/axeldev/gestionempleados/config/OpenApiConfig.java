package com.axeldev.gestionempleados.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gestión de Empleados API")
                        .description("REST API para gestión de empleados con paginación, filtros dinámicos y manejo global de errores.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Axel de la Canal")
                                .email("axel.delacanal.aedlc@gmail.com")
                                .url("https://github.com/AxeldelaCanal")));
    }
}
