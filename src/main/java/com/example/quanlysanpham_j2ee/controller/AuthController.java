package com.example.quanlysanpham_j2ee.controller;

import com.example.quanlysanpham_j2ee.security.Role;
import com.example.quanlysanpham_j2ee.security.RoleRepository;
import com.example.quanlysanpham_j2ee.security.User;
import com.example.quanlysanpham_j2ee.security.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String confirmPassword,
                             Model model) {
        if (username == null || username.trim().length() < 3) {
            model.addAttribute("error", "Tên đăng nhập phải có ít nhất 3 ký tự.");
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "auth/register";
        }
        if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            model.addAttribute("error", "Email không hợp lệ.");
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "auth/register";
        }
        if (password == null || password.length() < 6) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự.");
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "auth/register";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "auth/register";
        }
        if (userRepository.existsByUsername(username.trim())) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại.");
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "auth/register";
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("USER");
                    return roleRepository.save(role);
                });
        user.getRoles().add(userRole);
        userRepository.save(user);

        return "redirect:/login?registered";
    }
}
