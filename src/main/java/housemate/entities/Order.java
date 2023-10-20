/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "is_complete")
    private boolean isComplete;

    @Column(name = "final_price")
    private int finalPrice;

    @Column(name = "sub_total")
    private int subTotal;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "transaction_date")
    private String transactionDate;

    @Transient
    private int discountPrice;

    @Transient
    private UserAccount user;

    @Transient
    private List<OrderItem> listOrderItem;

}