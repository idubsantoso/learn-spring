package com.zarszz.userservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import com.zarszz.userservice.domain.enumData.PaymentMethod;
import com.zarszz.userservice.domain.enumData.PaymentStatus;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity()
@Table(name = "payments", indexes = {
    @Index(name = "payment_code_idx", columnList = "payment_code", unique = true)
})
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonBinaryType.class)
})
@Getter
@Setter
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

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private TransactionDetails transactionDetails;

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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(columnDefinition="bigint", name = "order_id", referencedColumnName = "id")
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(columnDefinition="bigint", name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;
}
