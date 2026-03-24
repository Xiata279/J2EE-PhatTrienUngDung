# Bài Tập Thực Hành Tuần 5 & 6 - E-Commerce

Chào cậu, dưới đây là các đoạn code mình đã hoàn thiện cho các chức năng Tìm kiếm, Phân trang, Sắp xếp, Lọc, Giỏ hàng và Thanh toán như cấu trúc project hiện tại nhé. Cậu xem rồi copy vào các class tương ứng nha.

## 1. Repository & Service (Xử lý DB và Logic)

### `ProductRepository.java`
Thêm các hàm dùng cho phân trang và tìm kiếm.

```java
package com.example.quanlysanpham_j2ee.repository;

import com.example.quanlysanpham_j2ee.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Tìm sản phẩm theo keyword, có phân trang
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    // Lọc theo danh mục, có phân trang
    Page<Product> findByCategoryId(int categoryId, Pageable pageable);
}
```

### `ProductService.java`
Thêm hàm xử lý kết hợp tìm kiếm, lọc, phân trang và sắp xếp.

```java
    // Import các thư viện này nha
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;

    // ... (Giữ nguyên các code cũ)

    // Xử lý logic hiển thị danh sách ở trang Shop
    public Page<Product> getProductsForShop(String keyword, Integer categoryId, int pageNo, String sortField, String sortDir) {
        // Cấu hình sắp xếp (Tăng dần hay giảm dần theo field)
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        
        // Phân trang: hiển thị 5 item/trang. (pageNo - 1 vì Spring Data JPA tính trang từ 0)
        Pageable pageable = PageRequest.of(pageNo - 1, 5, sort);
        
        // Kiểm tra xem user đang dùng tính năng gì để gọi repository phù hợp
        if (keyword != null && !keyword.trim().isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId, pageable);
        }
        
        // Mặc định lấy tất cả có phân trang
        return productRepository.findAll(pageable);
    }
```

---

## 2. Model và Service cho Đặt Hàng (Checkout)

Cậu tạo thêm mấy class này trong package `model` và `service` nhé.

### `CartItem.java` (Class tạm để lưu giỏ hàng trong Session)
```java
package com.example.quanlysanpham_j2ee.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private int productId;
    private String name;
    private long price;
    private int quantity;
}
```

### `Order.java` (Bảng Order)
```java
package com.example.quanlysanpham_j2ee.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String customerName;
    private String address;
    private String phone;
    private Date orderDate;
    private double totalAmount;
}
```

### `OrderDetail.java` (Bảng Order Detail)
```java
package com.example.quanlysanpham_j2ee.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private int productId;
    private int quantity;
    private long price;
}
```

*(Cậu nhớ tạo thêm `OrderRepository` và `OrderDetailRepository` extends `JpaRepository` giống như `ProductRepository` nha).*

### `OrderService.java` (Xử lý lưu DB khi đặt hàng)
```java
package com.example.quanlysanpham_j2ee.service;

import com.example.quanlysanpham_j2ee.model.CartItem;
import com.example.quanlysanpham_j2ee.model.Order;
import com.example.quanlysanpham_j2ee.model.OrderDetail;
import com.example.quanlysanpham_j2ee.repository.OrderDetailRepository;
import com.example.quanlysanpham_j2ee.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    // Dùng Transactional để đảm bảo lưu Order và OrderDetail cùng thành công
    @Transactional
    public void placeOrder(List<CartItem> cart, String customerName, String address, String phone) {
        // Tính tổng tiền
        double total = cart.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        
        // Tạo và lưu Order mới
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setAddress(address);
        order.setPhone(phone);
        order.setOrderDate(new Date());
        order.setTotalAmount(total);
        
        Order savedOrder = orderRepository.save(order);
        
        // Lưu từng sản phẩm vào Order Detail
        for (CartItem item : cart) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProductId(item.getProductId());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());
            orderDetailRepository.save(detail);
        }
    }
}
```

---

## 3. Controller 

### `HomeController.java` (Cập nhật router trang Shop)
Ghi đè lại API `/shop` cũ của cậu.

```java
    import org.springframework.data.domain.Page;
    
    @GetMapping("/shop")
    public String shop(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) Integer categoryId,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
        
        // Lấy dữ liệu đã phân trang & lọc
        Page<Product> page = productService.getProductsForShop(keyword, categoryId, pageNo, sortField, sortDir);
        
        // Đẩy dữ liệu sang Thymeleaf
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc"); // Để thực hiện toggle asc/desc
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        
        model.addAttribute("products", page.getContent());
        model.addAttribute("categories", categoryService.getAll());
        
        return "shop";
    }
```

### `CartController.java` (Tạo mới để quản lý giỏ hàng)
```java
package com.example.quanlysanpham_j2ee.controller;

import com.example.quanlysanpham_j2ee.model.CartItem;
import com.example.quanlysanpham_j2ee.model.Product;
import com.example.quanlysanpham_j2ee.service.OrderService;
import com.example.quanlysanpham_j2ee.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    // Xem giỏ hàng hiện tại
    @GetMapping("")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        
        // Tính tổng tiền toàn giỏ hàng
        double total = cart.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        
        model.addAttribute("cartItems", cart);
        model.addAttribute("totalPrice", total);
        return "cart"; // Trả về file cart.html
    }

    // Thêm sản phẩm vào giỏ
    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") int productId, 
                            @RequestParam("quantity") int quantity, 
                            HttpSession session) {
        Product product = productService.get(productId);
        if (product != null) {
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
            }
            
            // Logic cộng dồn nếu sản phẩm đã có trong giỏ
            boolean exists = false;
            for (CartItem item : cart) {
                if (item.getProductId() == productId) {
                    item.setQuantity(item.getQuantity() + quantity);
                    exists = true;
                    break;
                }
            }
            // Nếu chưa có thì add mới
            if (!exists) {
                cart.add(new CartItem(product.getId(), product.getName(), product.getPrice(), quantity));
            }
            
            // Lưu lại session
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    // Xử lý khi nhấn nút Đặt Hàng
    @PostMapping("/checkout")
    public String checkout(HttpSession session, 
                           @RequestParam("customerName") String customerName,
                           @RequestParam("address") String address,
                           @RequestParam("phone") String phone) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null && !cart.isEmpty()) {
            // Lưu xuống CSDL
            orderService.placeOrder(cart, customerName, address, phone);
            // Xóa giỏ hàng khỏi session sau khi mua xong
            session.removeAttribute("cart");
        }
        return "redirect:/cart?success";
    }
}
```

---

## 4. Giao diện (Thymeleaf/HTML)

### Cập nhật `shop.html` (Phần form tìm kiếm, sắp xếp, phân trang và nút Add to cart)
Chèn vào đúng vị trí cần thiết trong `shop.html` cũ nhé.

**Khung Search + Sort:**
```html
<form th:action="@{/shop}" method="get" class="mb-4">
    <div class="row gx-2 gy-2 align-items-center">
        <!-- Từ khóa tìm kiếm -->
        <div class="col-md-5">
            <input type="text" name="keyword" class="form-control" placeholder="Nhập tên sản phẩm..." th:value="${keyword}">
        </div>
        <!-- Dropdown chọn danh mục (có thể giữ lại filter cũ hoặc dùng cái này) -->
        <div class="col-md-3">
            <select name="category" class="form-select">
                <option value="">-- Tất cả danh mục --</option>
                <option th:each="cat : ${categories}" th:value="${cat.id}" th:text="${cat.name}" th:selected="${selectedCategoryId == cat.id}"></option>
            </select>
        </div>
        <!-- Dropdown sắp xếp -->
        <div class="col-md-2">
            <select name="sortField" class="form-select">
                <option value="id" th:selected="${sortField == 'id'}">Mới nhất</option>
                <option value="price" th:selected="${sortField == 'price'}">Giá</option>
            </select>
            <input type="hidden" name="sortDir" th:value="${sortDir}">
        </div>
        <!-- Submit Button -->
        <div class="col-md-2">
            <button type="submit" class="btn btn-primary w-100">Lọc & Tìm</button>
        </div>
    </div>
    <!-- Nút đổi chiều sắp xếp (Tăng/Giảm) -->
    <div class="mt-2 text-end">
        <a th:href="@{/shop(keyword=${keyword}, category=${selectedCategoryId}, pageNo=${currentPage}, sortField=${sortField}, sortDir=${reverseSortDir})}" 
           class="btn btn-sm btn-outline-secondary">
           Đổi chiều sắp xếp (Đang xếp: <!-- hiển thị text asc / desc (bạn tự custom) --> [[${sortDir}]])
        </a>
    </div>
</form>
```

**Nút Add To Cart (Dưới mỗi sản phẩm trong vòng lặp `th:each`):**
```html
<form th:action="@{/cart/add}" method="post">
    <input type="hidden" name="productId" th:value="${product.id}">
    <input type="hidden" name="quantity" value="1"> <!-- Mặc định mua 1 -->
    <button type="submit" class="btn btn-sm btn-success mt-2 w-100">
        <i class="fas fa-shopping-cart"></i> Add to Cart
    </button>
</form>
```

**Phân trang (Đặt bên dưới danh sách sản phẩm):**
```html
<div th:if="${totalPages > 1}" class="d-flex justify-content-center mt-4">
    <ul class="pagination">
        <!-- Nút Previous -->
        <li class="page-item" th:classappend="${currentPage == 1} ? 'disabled'">
            <a class="page-link" th:href="@{/shop(keyword=${keyword}, category=${selectedCategoryId}, pageNo=${currentPage - 1}, sortField=${sortField}, sortDir=${sortDir})}">Previous</a>
        </li>
        
        <!-- Các trang -->
        <li class="page-item" th:each="i : ${#numbers.sequence(1, totalPages)}" th:classappend="${currentPage == i} ? 'active'">
            <a class="page-link" th:href="@{/shop(keyword=${keyword}, category=${selectedCategoryId}, pageNo=${i}, sortField=${sortField}, sortDir=${sortDir})}" th:text="${i}"></a>
        </li>
        
        <!-- Nút Next -->
        <li class="page-item" th:classappend="${currentPage == totalPages} ? 'disabled'">
            <a class="page-link" th:href="@{/shop(keyword=${keyword}, category=${selectedCategoryId}, pageNo=${currentPage + 1}, sortField=${sortField}, sortDir=${sortDir})}">Next</a>
        </li>
    </ul>
</div>
```

### `cart.html` (Tạo file view mới cho giỏ hàng)
Lưu file này trong thư mục `templates/cart.html` nhé.

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout" layout:decorate="~{_layout}">
<head><title>Giỏ hàng của bạn</title></head>
<body>
<div layout:fragment="content">
    <div class="mb-4">
        <h4 class="fw-bold mb-1" style="color:#0f172a;">Giỏ hàng của bạn</h4>
    </div>

    <!-- Thông báo đặt hàng thành công -->
    <div th:if="${param.success}" class="alert alert-success">
        Cảm ơn cậu! Đơn hàng đã được đặt thành công.
    </div>

    <div class="row">
        <!-- Bảng liệt kê sản phẩm -->
        <div class="col-md-8">
            <div class="table-responsive">
                <table class="table table-bordered text-center align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Tên sản phẩm</th>
                            <th>Đơn giá</th>
                            <th>Số lượng</th>
                            <th>Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr th:each="item : ${cartItems}">
                            <td th:text="${item.name}"></td>
                            <td th:text="${#numbers.formatInteger(item.price, 0, 'COMMA')} + ' đ'"></td>
                            <td th:text="${item.quantity}"></td>
                            <!-- Tính thành tiền 1 dòng -->
                            <td th:text="${#numbers.formatInteger(item.price * item.quantity, 0, 'COMMA')} + ' đ'"></td>
                        </tr>
                        <tr th:if="${#lists.isEmpty(cartItems)}">
                            <td colspan="4">Giỏ hàng đang trống.</td>
                        </tr>
                    </tbody>
                    <tfoot th:unless="${#lists.isEmpty(cartItems)}">
                        <tr>
                            <td colspan="3" class="text-end fw-bold">Tổng tiền cả giỏ hàng:</td>
                            <td class="fw-bold text-danger" th:text="${#numbers.formatInteger(totalPrice, 0, 'COMMA')} + ' đ'"></td>
                        </tr>
                    </tfoot>
                </table>
            </div>
            <a href="/shop" class="btn btn-outline-primary">Tiếp tục mua sắm</a>
        </div>

        <!-- Form Đặt hàng -->
        <div class="col-md-4" th:unless="${#lists.isEmpty(cartItems)}">
            <div class="card p-3 shadow-sm">
                <h5 class="fw-bold mb-3">Thông tin giao hàng</h5>
                <form th:action="@{/cart/checkout}" method="post">
                    <div class="mb-3">
                        <label class="form-label">Họ Tên</label>
                        <input type="text" name="customerName" class="form-control" required placeholder="Nhập tên người nhận">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Số điện thoại</label>
                        <input type="text" name="phone" class="form-control" required placeholder="Nhập SĐT">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Địa chỉ</label>
                        <textarea name="address" class="form-control" rows="3" required placeholder="Nhập địa chỉ nhận hàng"></textarea>
                    </div>
                    <button type="submit" class="btn btn-success w-100 fw-bold">Tiến hành đặt hàng</button>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
```

Cậu cứ bám sát các đoạn này nhé! Code viết logic rất thuần túy dễ đọc để bám đúng nghiệp vụ. Chúc cậu thực hành suôn sẻ nha!
