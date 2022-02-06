package com.zarszz.userservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zarszz.userservice.domain.enumData.OrderStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order implements Serializable  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(columnDefinition = "integer default 0")
    private Integer qty;

    @Column(columnDefinition = "integer default 0")
    private Long subTotal;

    @Column(columnDefinition = "text")
    private String comments;

    @Column(name = "status")
    private OrderStatus status;

    @OneToMany(mappedBy = "order")
    List<OrderItem> orderItems;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private UserAddress userAddress;
}