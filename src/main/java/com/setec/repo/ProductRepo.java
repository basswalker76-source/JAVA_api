package com.setec.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.setec.entities.Product;

public interface ProductRepo extends JpaRepository<Product, Integer>{

}
