package com.wintersports.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "health")
@Data
@EqualsAndHashCode(callSuper = true)
public class Health extends BaseEntity {
    private String status;
}