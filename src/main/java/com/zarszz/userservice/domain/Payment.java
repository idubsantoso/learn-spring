package com.zarszz.userservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zarszz.userservice.domain.enumData.PaymentMethod;
import com.zarszz.userservice.domain.enumData.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity()
@Table(name = "payments", indexes = {
    @Index(name = "payment_code_idx", columnList = "payment_code", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @Column(name = "payment_code")
    private String paymentCode;

    @Column
    private Long total;

    @Column
    private String redirectUrl;

    @Enumerated(EnumType.ORDINAL)
    @Column
    private PaymentStatus status;

    @Column
    private PaymentMethod method;

    @Column
    private Date PaymentDate;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(columnDefinition="bigint", name = "order_id", referencedColumnName = "id")
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(columnDefinition="bigint", name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;
}
