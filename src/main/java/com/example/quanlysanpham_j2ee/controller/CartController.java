package com.example.quanlysanpham_j2ee.controller;

import com.example.quanlysanpham_j2ee.model.CartItem;
import com.example.quanlysanpham_j2ee.model.Order;
import com.example.quanlysanpham_j2ee.model.Product;
import com.example.quanlysanpham_j2ee.security.User;
import com.example.quanlysanpham_j2ee.security.UserRepository;
import com.example.quanlysanpham_j2ee.service.OrderService;
import com.example.quanlysanpham_j2ee.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        double total = cart.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        model.addAttribute("cartItems", cart);
        model.addAttribute("totalPrice", total);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") int productId,
                            @RequestParam("quantity") int quantity,
                            HttpSession session) {
        Product product = productService.get(productId);
        if (product == null) return "redirect:/shop";

        List<CartItem> cart = getCart(session);

        boolean found = false;
        for (CartItem item : cart) {
            if (item.getProductId() == productId) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            cart.add(new CartItem(product.getId(), product.getName(), product.getPrice(), quantity));
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam("productId") int productId,
                                  @RequestParam("quantity") int quantity,
                                  HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (quantity <= 0) {
            cart.removeIf(item -> item.getProductId() == productId);
        } else {
            for (CartItem item : cart) {
                if (item.getProductId() == productId) {
                    item.setQuantity(quantity);
                    break;
                }
            }
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{productId}")
    public String removeItem(@PathVariable int productId, HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProductId() == productId);
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session,
                           @RequestParam("customerName") String customerName,
                           @RequestParam("address") String address,
                           @RequestParam("phone") String phone,
                           Principal principal) {
        List<CartItem> cart = getCart(session);
        if (cart.isEmpty()) return "redirect:/cart";

        String username = (principal != null) ? principal.getName() : null;
        orderService.placeOrder(cart, customerName, address, phone, username);
        session.removeAttribute("cart");
        return "redirect:/cart?success";
    }

    @GetMapping("/orders")
    public String orderHistory(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) return "redirect:/login";

        List<Order> orders = orderService.getOrdersByUserId(user.getId());
        model.addAttribute("orders", orders);
        return "order_history";
    }

    @PostMapping("/orders/cancel/{orderId}")
    public String cancelOrder(@PathVariable int orderId, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            orderService.cancelOrder(orderId, user.getId());
        }
        return "redirect:/cart/orders";
    }

    @GetMapping("/orders/detail/{orderId}")
    public String orderDetail(@PathVariable int orderId, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) return "redirect:/login";

        Order order = orderService.getOrderById(orderId);
        if (order == null || order.getUser() == null || !order.getUser().getId().equals(user.getId())) {
            return "redirect:/cart/orders";
        }

        model.addAttribute("order", order);
        return "order_detail";
    }

    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
}
