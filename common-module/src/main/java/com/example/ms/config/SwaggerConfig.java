package com.example.ms.config;

import com.example.tools.utils.Constant;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String appVersion) {
        Components components = new Components();
        SecurityScheme token = new SecurityScheme()
                .name(Constant.SECURITY_HEADER_TOKEN)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER);
        SecurityScheme username = new SecurityScheme()
                .name(Constant.SECURITY_HEADER_USERNAME)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER);
        Map<String, SecurityScheme> securitySchemes = new HashMap<>();
        securitySchemes.put(Constant.SECURITY_HEADER_TOKEN, token);
        securitySchemes.put(Constant.SECURITY_HEADER_USERNAME, username);
        components.setSecuritySchemes(securitySchemes);
        List<SecurityRequirement> securityRequirements = new ArrayList<>();
        securityRequirements.add(new SecurityRequirement().addList(Constant.SECURITY_HEADER_TOKEN));
        securityRequirements.add(new SecurityRequirement().addList(Constant.SECURITY_HEADER_USERNAME));
        return new OpenAPI()
                .components(components)
                .info(new Info()
                        .title("Management System API")
                        .description("Management System server api. 管理系统后台API.")
                        .version(appVersion)
                        .license(new License()
                                .name("Apache2.0")
                                .url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentation")
                        .url("https://www.jianshu.com/nb/41542276"))
                .security(securityRequirements);
    }
}
