package com.policyhub;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.policyhub.entity.User;
import com.policyhub.entity.User.Role;
import com.policyhub.repository.UserRepository;

@SpringBootApplication
public class PolicyHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolicyHubApplication.class,args);
		
	}	
		
		@Bean
	    CommandLineRunner createDefaultAdmin(UserRepository userRepository) {
	        return args -> {
	            // Check if admin already exists
	            Optional<User> existingAdmin = userRepository.findByUsername("admin");
	            if (existingAdmin == null) {
	                User admin = new User();
	                admin.setUsername("admin");
	                admin.setPassword("admin"); // Plain for now (can encrypt later)
	                admin.setRole(Role.ADMIN);

	                userRepository.save(admin);
	                System.out.println("✅ Default Admin created -> username: admin | password: admin123");
	            }
	        };
	    

	}

}
