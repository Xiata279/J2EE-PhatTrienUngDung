package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    //Trang chu root
    @GetMapping("/")
    public String root() 
    {
        return "index";
    }

    @GetMapping("/home")
    public String index() 
    {
        String tenTrang = "index";
        return tenTrang;
    }
}
