package com.setec.controller;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.setec.entities.PostProductDAO;
import com.setec.entities.Product;
import com.setec.entities.PutProductDAO;
import com.setec.repo.ProductRepo;
import com.setec.service.ProductService;

import jakarta.validation.Valid;

//http://localhost:8080/swagger-ui/index.html

@RestController
@RequestMapping("/api/product")
public class MyController {
	
	

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepo productRepo;

	@GetMapping
	public Object getAll() {
		var products = productRepo.findAll();
		if (products.isEmpty()) {
			return ResponseEntity.status(404).body(Map.of("message", "Product is Empty"));

		}
		return products;
	}

//	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	public ResponseEntity<?> addProduct(@ModelAttribute PostProductDAO postProductDAO) throws Exception {
//		String uploadDir = new File("myApp/static").getAbsolutePath();
//		File dir = new File(uploadDir);
//		if (!dir.exists()) {
//			dir.mkdirs();
//		}
//		var file = postProductDAO.getFile();
//
//		productService.validateImage(file);
//		String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//		String filePath = Paths.get(uploadDir, uniqueName).toString();
//
//		file.transferTo(new File(filePath));
//
//		var pro = new Product();
//		pro.setName(postProductDAO.getName());
//		pro.setPrice(postProductDAO.getPrice());
//		pro.setQty(postProductDAO.getQty());
//		pro.setImageURL("/static/" + uniqueName);
//
//		productRepo.save(pro);
//
//		return ResponseEntity.status(201).body(pro);
//	}
	
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addProduct(
	        @Valid @ModelAttribute PostProductDAO postProductDAO,
	        BindingResult bindingResult
	) throws Exception {

	    Map<String, String> errors = new HashMap<>();

	    bindingResult.getFieldErrors().forEach(err ->
	            errors.put(err.getField(), err.getDefaultMessage())
	    );

	    MultipartFile file = postProductDAO.getFile();
	    if (file == null || file.isEmpty()) {
	        errors.put("file", "Image file is required");
	    }

	    if (!errors.isEmpty()) {
	        return ResponseEntity.badRequest().body(errors);
	    }

	    String uploadDir = new File("myApp/static").getAbsolutePath();
	    File dir = new File(uploadDir);
	    if (!dir.exists()) {
	        dir.mkdirs();
	    }

	    productService.validateImage(file);

	    String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
	    String filePath = Paths.get(uploadDir, uniqueName).toString();
	    file.transferTo(new File(filePath));

	    Product product = new Product();
	    product.setName(postProductDAO.getName());
	    product.setPrice(postProductDAO.getPrice());
	    product.setQty(postProductDAO.getQty());
	    product.setImageURL("/static/" + uniqueName);

	    productRepo.save(product);

	    return ResponseEntity.status(201).body(product);
	}


//	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	public ResponseEntity<?> updateProduct(@ModelAttribute PutProductDAO putProductDAO) throws Exception {
//		var p = productRepo.findById(putProductDAO.getId());
//
//		if (p.isPresent()) {
//			var update = p.get();
//			update.setName(putProductDAO.getName());
//			update.setPrice(putProductDAO.getPrice());
//			update.setQty(putProductDAO.getQty());
//			if (putProductDAO.getFile() != null) {
//				String uploadDir = new File("myApp/static").getAbsolutePath();
//				File dir = new File(uploadDir);
//				if (!dir.exists()) {
//					dir.mkdirs();
//				}
//
//				var file = putProductDAO.getFile();
//				productService.validateImage(file);
//				String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//				String filePath = Paths.get(uploadDir, uniqueName).toString();
//
//				new File("myApp/" + update.getImageURL()).delete();
//				file.transferTo(new File(filePath));
//				update.setImageURL("/static/" + uniqueName);
//			}
//
//			productRepo.save(update);
//
//			return ResponseEntity.status(HttpStatus.ACCEPTED).body(p.get());
//		}
//
//		return ResponseEntity.status(404)
//				.body(Map.of("message", "Product id = " + putProductDAO.getId() + " Not Found"));
//	}
	
	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateProduct(@ModelAttribute @Valid PutProductDAO dao, BindingResult result) throws Exception {
	    
	    if (result.hasErrors()) {
	        List<String> errors = result.getFieldErrors().stream()
	                .map(err -> err.getField() + ": " + err.getDefaultMessage())
	                .toList();
	        return ResponseEntity.badRequest().body(Map.of("errors", errors));
	    }

	    var optionalProduct = productRepo.findById(dao.getId());
	    if (optionalProduct.isEmpty()) {
	        return ResponseEntity.status(404).body(Map.of("message", "Product not found"));
	    }

	    Product existing = optionalProduct.get();
	    boolean isChanged = false;

	    if (dao.getName() != null && !dao.getName().isBlank() && !dao.getName().equals(existing.getName())) {
	        existing.setName(dao.getName());
	        isChanged = true;
	    }

	    if (dao.getPrice() != null && Double.compare(dao.getPrice(), existing.getPrice()) != 0) {
	        existing.setPrice(dao.getPrice());
	        isChanged = true;
	    }

	    if (dao.getQty() != null && !dao.getQty().equals(existing.getQty())) {
	        existing.setQty(dao.getQty());
	        isChanged = true;
	    }

	    if (dao.getFile() != null && !dao.getFile().isEmpty()) {
	        String newFileName = dao.getFile().getOriginalFilename();
	        String uploadDir = new File("myApp/static").getAbsolutePath();

	        if (existing.getImageURL() == null || !existing.getImageURL().endsWith(newFileName)) {
	            if (existing.getImageURL() != null) {
	                new File("myApp/" + existing.getImageURL()).delete();
	            }

	            String uniqueName = UUID.randomUUID() + "_" + newFileName;
	            dao.getFile().transferTo(new File(Paths.get(uploadDir, uniqueName).toString()));
	            existing.setImageURL("/static/" + uniqueName);
	            isChanged = true;
	        }
	    }

	    if (!isChanged) {
	        return ResponseEntity.ok(Map.of("message", "No Data is Changed"));
	    }

	    productRepo.save(existing);
	    return ResponseEntity.accepted().body(existing);
	}
	

	@GetMapping({ "{id}", "/id/{id}" })
	public ResponseEntity<?> getById(@PathVariable("id") Integer id) {
		var pro = productRepo.findById(id);
		if (pro.isPresent())
			return ResponseEntity.status(200).body(pro.get());

		return ResponseEntity.status(404).body(Map.of("message", "Product id = " + id + " not found"));
	}

//	@DeleteMapping({ "{id}", "/id/{id}" })
//	public ResponseEntity<?> deleteById(@PathVariable("id") Integer id) {
//		var pro = productRepo.findById(id);
//		if (pro.isPresent()) {
//			new File("myApp/"+pro.get().getImageURL()).delete();
//			productRepo.delete(pro.get());
//			return ResponseEntity.status(HttpStatus.ACCEPTED)
//					.body(Map.of("message", "Product id = "+id+" has been deleted!"));
//
//		}
//
//		return ResponseEntity.status(404).body(Map.of("message", "Product id = " + id + " not found"));
//
//	}
	
	@DeleteMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> deleteById(@RequestParam("id") Integer id) {
	    var pro = productRepo.findById(id);
	    if (pro.isPresent()) {
	        new File("myApp/" + pro.get().getImageURL()).delete();
	        productRepo.delete(pro.get());
	        return ResponseEntity.status(HttpStatus.ACCEPTED)
	            .body(Map.of("message", "Product id = " + id + " has been deleted!"));
	    }

	    return ResponseEntity.status(404).body(Map.of("message", "Product id = " + id + " not found"));
	}
}
