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
public class PostProductDAO {
	
	@NotBlank(message = "Product name is required")
	private String name;
	
	
	@NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;
	
	@NotNull(message = "Quantity is required")
    @Positive(message = "Quantity should have at lease 1")
	private Integer qty;
	
	@NotNull(message = "Image file is required")
	private MultipartFile file;
}
