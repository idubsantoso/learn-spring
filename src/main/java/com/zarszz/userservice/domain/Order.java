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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(columnDefinition="bigint", name = "user_id")
    @JsonIgnore
    private User user;

    @Column(columnDefinition = "integer default 0")
    private Integer qty;

    @Column(columnDefinition = "integer default 0")
    private Long subTotal;

    @Column(columnDefinition = "text")
    private String comments;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private OrderStatus status;

    @OneToMany(mappedBy = "order")
    List<OrderItem> orderItems;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(columnDefinition="integer", name = "address_id", referencedColumnName = "id")
    private UserAddress userAddress;
}