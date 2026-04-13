package com.goktug.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "product_views")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "event")
    private String event;

    @Column(name = "messageid")
    private String messageId;

    @Column(name = "userid")
    private String userId;

    @Column(name = "productid")
    private String productId;

    @Column(name = "source")
    private String source;

    @Column(name = "timestamp")
    private LocalDateTime viewDate;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean isDeleted = false;
}