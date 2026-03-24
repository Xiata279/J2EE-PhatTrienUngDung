package com.example.quanlysanpham_j2ee.controller;

import com.example.quanlysanpham_j2ee.service.CategoryService;
import com.example.quanlysanpham_j2ee.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import com.example.quanlysanpham_j2ee.model.Product;

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
    public String shop(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) Integer categoryId,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
        
        Page<Product> page = productService.getProductsForShop(keyword, categoryId, pageNo, sortField, sortDir);
        
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        
        model.addAttribute("products", page.getContent());
        model.addAttribute("categories", categoryService.getAll());
        return "shop";
    }
}
