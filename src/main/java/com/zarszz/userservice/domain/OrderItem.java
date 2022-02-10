package com.zarszz.userservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "order_item")
@Data
@NoArgsConstructor
public class OrderItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "product_id")
    Product product;

    @ManyToOne()
    @JoinColumn(columnDefinition="bigint", name = "order_id")
    @JsonIgnore
    Order order;

    @Column
    private Integer qty;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}