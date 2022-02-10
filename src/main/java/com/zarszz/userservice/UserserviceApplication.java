package com.zarszz.userservice;

import com.zarszz.userservice.domain.Product;
import com.zarszz.userservice.domain.Role;
import com.zarszz.userservice.domain.User;
import com.zarszz.userservice.service.ProductService;
import com.zarszz.userservice.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zarszz.userservice"})
public class UserserviceApplication {

  public static void main(String[] args) {
    SpringApplication.run(UserserviceApplication.class, args);
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


  @Bean
  CommandLineRunner run(UserService userService, ProductService pService) {
    return args -> {
      userService.saveRole(new Role(null, "ROLE_USER"));
      userService.saveRole(new Role(null, "ROLE_MANAGER"));
      userService.saveRole(new Role(null, "ROLE_ADMIN"));
      userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

      userService.saveUser(new User(null, "John Travolta", "john", "1234", new ArrayList<>()));
      userService.saveUser(new User(null, "Will Smith", "will", "1234", new ArrayList<>()));
      userService.saveUser(new User(null, "Jim Carry", "jim", "1234", new ArrayList<>()));
      userService.saveUser(new User(null, "Arnold", "arnold", "1234", new ArrayList<>()));

      userService.addRoleToUser("john", "ROLE_USER");
      userService.addRoleToUser("john", "ROLE_MANAGER");
      userService.addRoleToUser("will", "ROLE_MANAGER");
      userService.addRoleToUser("jim", "ROLE_ADMIN");
      userService.addRoleToUser("arnold", "ROLE_SUPER_ADMIN");
      userService.addRoleToUser("arnold", "ROLE_ADMIN");
      userService.addRoleToUser("arnold", "ROLE_USER");

      for (int i = 0; i < 10; i++) {
        var product = new Product();
        product.setName("produk" + i);
        product.setDescription("produk" + i);
        product.setSku("produk" + i);
        product.setPrice((long) i * 1000);
        product.setStock(100);;
        pService.save(product);
      }
    };
  }
}
