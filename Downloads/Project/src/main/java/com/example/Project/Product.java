package com.example.Project;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product {
    @Id
    int id;

    int price;
    String name ;
}
