package com.example.Project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
public class procontrol {

    @Autowired
    private ProductService service;

    @GetMapping("/")
    public String hello() {
        return "hello";
    }

    @PostMapping("/save")
    public String saveProduct(@RequestBody List<Product> product) {
        service.save(product);
        return "Product saved!";
    }

    @GetMapping("/pro")
    public List<Product> prod(){
        return  service.get();
    }
    @PostMapping("/updprod/{prodid}")
    public  void update (@PathVariable int prodid, @RequestBody Product prod){

        service.update(prodid, prod);
    }
}
