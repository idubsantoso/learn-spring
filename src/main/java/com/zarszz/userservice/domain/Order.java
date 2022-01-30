package com.zarszz.userservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Integer subTotal;

    @Column(columnDefinition = "text")
    private String comments;

    @OneToMany(mappedBy = "order")
    List<OrderItem> orderItems;
}