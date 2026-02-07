package com.example.demo;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

//Service quan ly cac chuc nang lien quan den san pham
@Service
public class ProductService {
    // Danh sach san pham (luu tam trong bo nho)
    private List<Product> products = new ArrayList<>();
    private long nextId = 1;

    // Constructor khoi tao du lieu mau
    public ProductService() {
        // Them san pham mau de test
        products.add(new Product(1, "Laptop 1", 30000.0, "laptop.jpg", "Laptop"));
        products.add(new Product(2, "iPhone 15", 25000.0, "iphone.jpg", "Điện thoại"));
        nextId = 3;
    }

    // Lay tat ca cac san pham
    public List<Product> getAll() {
        return products;
    }

    // Them san pham moi
    public void add(Product product) {
        product.setId((int) nextId++);
        products.add(product);
    }

    // Tim san pham theo ID
    public Product get(int id) {
        return products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    // Cap nhat thong tin san pham
    public void update(Product updatedProduct) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == updatedProduct.getId()) {
                products.set(i, updatedProduct);
                break;
            }
        }
    }

    // Cap nhat anh san pham (Logic from screenshot hint)
    public void updateImage(int id, String image) {
        var product = get(id);
        if (product != null) {
            product.setImage(image);
        }
    }

    // Xoa san pham theo ID
    public void delete(int id) {
        products.removeIf(p -> p.getId() == id);
    }
}
