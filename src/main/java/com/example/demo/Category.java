package com.example.demo;

import jakarta.validation.constraints.NotBlank;

public class Category {
    private int id;
    
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;
    
    public Category() {
    }
    
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
