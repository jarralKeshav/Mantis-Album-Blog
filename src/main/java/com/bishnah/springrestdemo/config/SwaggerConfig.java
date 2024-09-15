package com.bishnah.springrestdemo.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Demo Api",
        version = "Version 1.0",
        contact = @Contact(
                name = "<YOUR-NAME>" , email = "<YOUR-EMAIL>", url = "<URL (if available)>"
        ),
        license = @License(
                name = "Apache 2.0" ,url = "https://www.apache.org/licenses/LICENSE-2.0"
        ),
        termsOfService = "https://www.apache.org/licenses/LICENSE-2.0.txt",
        description = "Spring Boot RestFul API Demo by Keshav Jrall"
)
)




public class SwaggerConfig {
}
