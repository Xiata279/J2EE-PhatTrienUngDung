package com.example.quanlysanpham_j2ee.controller;

import com.example.quanlysanpham_j2ee.model.Order;
import com.example.quanlysanpham_j2ee.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    // Trang quản lý đơn hàng của admin
    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/admin_orders";
    }

    // Admin cập nhật trạng thái đơn hàng
    @PostMapping("/update-status/{orderId}")
    public String updateStatus(@PathVariable int orderId,
                               @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return "redirect:/admin/orders";
    }

    // Admin xóa đơn hàng (hard delete)
    @PostMapping("/delete/{orderId}")
    public String deleteOrder(@PathVariable int orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order != null) {
            orderService.adminDeleteOrder(orderId);
        }
        return "redirect:/admin/orders";
    }
}
