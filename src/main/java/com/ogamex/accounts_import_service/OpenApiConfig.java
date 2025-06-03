package com.ogamex.accounts_import_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Accounts Import Service API",
                version = "0.0.1",
                description = "Endpoints for creating and managing OGame accounts"
        )
)
@Configuration
public class OpenApiConfig { }
