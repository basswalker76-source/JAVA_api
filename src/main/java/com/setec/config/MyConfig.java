package com.setec.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class MyConfig implements WebMvcConfigurer{
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**")
				.addResourceLocations("file:./myApp/static/");
				
		
				// For Linux Server
				//.addResourceLocations("file:/opt/myApp/static/");

	}
	@Bean
    public OpenAPI customOpenAPI() {
        // Explicitly define the server URL for Swagger UI API calls
    
        Server productionServer = new Server();
        productionServer.setUrl("https://cors.setecist.uk"); 
        productionServer.setDescription("Production Server");

        // You can add other servers (e.g., a local HTTP one for development)
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development Server");

        return new OpenAPI()
                .servers(List.of(productionServer, devServer))
                // Add other configurations like info, security schemes, etc.
                ;
    }

}
