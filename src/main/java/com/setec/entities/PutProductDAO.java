package com.setec.entities;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PutProductDAO {
    @NotNull(message = "ID is required")
    private Integer id;
    private String name;
    @Positive(message = "Price must be greater than 0")
    private Double price;
    @Positive(message = "Quantity should have at lease 1")
    private Integer qty;
    private MultipartFile file;
}
