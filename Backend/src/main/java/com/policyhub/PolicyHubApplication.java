package com.policyhub;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.policyhub.entity.User;
import com.policyhub.entity.User.Role;
import com.policyhub.repository.UserRepository;

@SpringBootApplication
public class PolicyHubApplication {

    
	@Bean
	public BCryptPasswordEncoder passEncoder() {
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(PolicyHubApplication.class,args);
		
	}	
	   
		
		@Bean
	    CommandLineRunner createDefaultAdmin(UserRepository userRepository,PasswordEncoder passEncoder) {
	        return args -> {
	            Optional<User> existingAdmin = userRepository.findByUsername("admin");
	            if (existingAdmin.isEmpty()) {
	                User admin = new User();
	                admin.setUsername("admin");
	                
	                admin.setPassword(passEncoder.encode("admin")); 
	                admin.setRole(Role.ADMIN);

	                userRepository.save(admin);
	                System.out.println(" Default Admin created -> username: admin | password: admin");
	            }
	        };
	    

	}

}
