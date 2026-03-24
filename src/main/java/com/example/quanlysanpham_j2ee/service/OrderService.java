package com.example.quanlysanpham_j2ee.service;

import com.example.quanlysanpham_j2ee.model.CartItem;
import com.example.quanlysanpham_j2ee.model.Order;
import com.example.quanlysanpham_j2ee.model.OrderDetail;
import com.example.quanlysanpham_j2ee.repository.OrderDetailRepository;
import com.example.quanlysanpham_j2ee.repository.OrderRepository;
import com.example.quanlysanpham_j2ee.security.User;
import com.example.quanlysanpham_j2ee.security.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void placeOrder(List<CartItem> cart, String customerName, String address, String phone, String username) {
        double total = cart.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setAddress(address);
        order.setPhone(phone);
        order.setOrderDate(new Date());
        order.setTotalAmount(total);
        order.setStatus("Chờ xác nhận");

        if (username != null) {
            User user = userRepository.findByUsername(username).orElse(null);
            order.setUser(user);
        }

        Order savedOrder = orderRepository.save(order);

        List<OrderDetail> details = new ArrayList<>();
        for (CartItem item : cart) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProductId(item.getProductId());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());
            details.add(detail);
        }
        orderDetailRepository.saveAll(details);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    public Order getOrderById(int orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Transactional
    public boolean cancelOrder(int orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return false;
        if (order.getUser() == null || !order.getUser().getId().equals(userId)) return false;
        if (!"Chờ xác nhận".equals(order.getStatus())) return false;

        orderRepository.delete(order);
        return true;
    }

    // Lấy toàn bộ đơn hàng cho admin
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    // Admin cập nhật trạng thái đơn hàng
    @Transactional
    public boolean updateOrderStatus(int orderId, String newStatus) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return false;
        order.setStatus(newStatus);
        orderRepository.save(order);
        return true;
    }

    // Admin xóa bất kỳ đơn hàng nào (không kiểm tra user)
    @Transactional
    public void adminDeleteOrder(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            orderRepository.delete(order);
        }
    }
}
