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

      String[] roles = {"ROLE_USER", "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"};

      for (var role: roles) {
        var r1 = new Role();
        r1.setName(role);
        userService.saveRole(r1);
      }

      var u1 = new User();
      u1.setName("John Travolta");
      u1.setUsername("john");
      u1.setPassword("1234");
      userService.saveUser(u1);

      var u2 = new User();
      u2.setName("Will Smith");
      u2.setUsername("will");
      u2.setPassword("1234");
      userService.saveUser(u2);

      var u3 = new User();
      u3.setName("Jim Carry");
      u3.setUsername("jim");
      u3.setPassword("1234");
      userService.saveUser(u3);

      var u4 = new User();
      u4.setName("Arnold");
      u4.setUsername("arnold");
      u4.setPassword("1234");
      userService.saveUser(u4);

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
