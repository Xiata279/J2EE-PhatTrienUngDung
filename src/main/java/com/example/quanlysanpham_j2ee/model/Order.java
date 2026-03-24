package com.example.quanlysanpham_j2ee.model;

import com.example.quanlysanpham_j2ee.security.User;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

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

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    private double totalAmount;

    @Column(length = 30)
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;
}
