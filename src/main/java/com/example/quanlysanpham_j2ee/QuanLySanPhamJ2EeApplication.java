package com.example.quanlysanpham_j2ee;

import com.example.quanlysanpham_j2ee.security.Role;
import com.example.quanlysanpham_j2ee.security.User;
import com.example.quanlysanpham_j2ee.security.RoleRepository;
import com.example.quanlysanpham_j2ee.security.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.HashSet;

@SpringBootApplication
public class QuanLySanPhamJ2EeApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuanLySanPhamJ2EeApplication.class, args);
    }

    @Bean
    CommandLineRunner initDefaultUser(RoleRepository roleRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role(null, "ADMIN", new HashSet<>()))); // tạo ROLE_ADMIN nếu thiếu

            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // mật khẩu mẫu
                admin.setEnabled(true);
                admin.getRoles().add(adminRole);
                userRepository.save(admin); // lưu user + gán role
            }
        };
    }
}
