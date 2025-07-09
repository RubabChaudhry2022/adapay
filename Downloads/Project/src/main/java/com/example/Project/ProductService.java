package com.example.Project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    public void save(List<Product> product) {
        repo.saveAll(product);
    }

    public List<Product> get() {
       return  repo.findAll();

    }

    public void update(int prodid, Product prod) {
        Optional<Product> pro1 = repo.findById(prodid);

        Product expro = pro1.get();
        expro.setName(prod.getName());
        expro.setPrice(prod.getPrice());
        repo.save(expro);
        System.out.println("prodid = " + prodid + ", prod = " + prod  + expro);




    }

}
