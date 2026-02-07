package com.example.demo;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private List<Category> categories = new ArrayList<>();
    private int nextId = 1;
    
    public CategoryService() {
        // Khởi tạo dữ liệu mẫu
        categories.add(new Category(1, "Lập trình"));
        categories.add(new Category(2, "Văn học"));
        categories.add(new Category(3, "Khoa học"));
        categories.add(new Category(4, "Lịch sử"));
        categories.add(new Category(5, "Tiểu thuyết"));
        categories.add(new Category(6, "Thiếu nhi"));
        nextId = 7;
    }
    
    public List<Category> getAllCategories() {
        return categories;
    }
    
    public Category getCategoryById(int id) {
        return categories.stream()
            .filter(cat -> cat.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    public void addCategory(Category category) {
        category.setId(nextId++);
        categories.add(category);
    }
    
    public void updateCategory(Category category) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == category.getId()) {
                categories.set(i, category);
                break;
            }
        }
    }
    
    public void deleteCategory(int id) {
        categories.removeIf(cat -> cat.getId() == id);
    }
}
