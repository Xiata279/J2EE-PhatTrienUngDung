package com.example.quanlysanpham_j2ee.controller;

import com.example.quanlysanpham_j2ee.service.CategoryService;
import com.example.quanlysanpham_j2ee.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredProducts", productService.getAll());
        return "home";
    }

    @GetMapping("/shop")
    public String shop(@RequestParam(value = "category", required = false) Integer categoryId, Model model) {
        if (categoryId != null) {
            model.addAttribute("products", productService.getByCategoryId(categoryId));
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            model.addAttribute("products", productService.getAll());
        }
        model.addAttribute("categories", categoryService.getAll());
        return "shop";
    }
}
