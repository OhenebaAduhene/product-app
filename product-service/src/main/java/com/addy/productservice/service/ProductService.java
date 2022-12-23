package com.addy.productservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.addy.productapi.core.product.Product;
import com.addy.productapi.exceptions.InvalidInputException;
import com.addy.productapi.exceptions.NotFoundException;
import com.addy.productutil.ServiceUtil;

@Service
public class ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public ProductService(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    public Product getProdut(int productId) {
        LOG.debug("/product return the found product for productId={}", productId);

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        if (productId == 13) {
            throw new NotFoundException("No product found for productId: " + productId);
        }

        return new Product(productId, "name-" + productId, 123, serviceUtil.getServiceAddress());
    }
}
