package com.fetch.receipt_processor.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "receipts")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Receipt {

    @Id
    private String id;
    private int points;

    @PrePersist
    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }

    public Receipt(int points) {
        this.points = points;
    }
}