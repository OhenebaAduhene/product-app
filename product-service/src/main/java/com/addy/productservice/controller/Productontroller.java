package com.addy.productservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.addy.productapi.core.product.Product;
import com.addy.productservice.service.ProductService;

@RestController
public class Productontroller {

   
    private final ProductService productService;

    @Autowired
    public Productontroller(ProductService productService){
        this.productService = productService;
    }

    @GetMapping("/product/{productId}")
    public Product getProduct( @PathVariable int productId){
        return productService.getProdut(productId);
    }

}
