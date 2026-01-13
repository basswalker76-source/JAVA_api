package com.setec.controller;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.setec.entities.PostProductDAO;
import com.setec.entities.Product;
import com.setec.entities.PutProductDAO;
import com.setec.repo.ProductRepo;
import com.setec.service.ProductService;

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

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addProduct(@ModelAttribute PostProductDAO postProductDAO) throws Exception {
		String uploadDir = new File("myApp/static").getAbsolutePath();
		File dir = new File(uploadDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		var file = postProductDAO.getFile();

		productService.validateImage(file);
		String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
		String filePath = Paths.get(uploadDir, uniqueName).toString();

		file.transferTo(new File(filePath));

		var pro = new Product();
		pro.setName(postProductDAO.getName());
		pro.setPrice(postProductDAO.getPrice());
		pro.setQty(postProductDAO.getQty());
		pro.setImageURL("/static/" + uniqueName);

		productRepo.save(pro);

		return ResponseEntity.status(201).body(pro);
	}

	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateProduct(@ModelAttribute PutProductDAO putProductDAO) throws Exception {
		var p = productRepo.findById(putProductDAO.getId());

		if (p.isPresent()) {
			var update = p.get();
			update.setName(putProductDAO.getName());
			update.setPrice(putProductDAO.getPrice());
			update.setQty(putProductDAO.getQty());
			if (putProductDAO.getFile() != null) {
				String uploadDir = new File("myApp/static").getAbsolutePath();
				File dir = new File(uploadDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}

				var file = putProductDAO.getFile();
				productService.validateImage(file);
				String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
				String filePath = Paths.get(uploadDir, uniqueName).toString();

				new File("myApp/" + update.getImageURL()).delete();
				file.transferTo(new File(filePath));
				update.setImageURL("/static/" + uniqueName);
			}

			productRepo.save(update);

			return ResponseEntity.status(HttpStatus.ACCEPTED).body(p.get());
		}

		return ResponseEntity.status(404)
				.body(Map.of("message", "Product id = " + putProductDAO.getId() + " Not Found"));
	}

	@GetMapping({ "{id}", "/id/{id}" })
	public ResponseEntity<?> getById(@PathVariable("id") Integer id) {
		var pro = productRepo.findById(id);
		if (pro.isPresent())
			return ResponseEntity.status(200).body(pro.get());

		return ResponseEntity.status(404).body(Map.of("message", "Product id = " + id + " not found"));
	}

	@DeleteMapping({ "{id}", "/id/{id}" })
	public ResponseEntity<?> deleteById(@PathVariable("id") Integer id) {
		var pro = productRepo.findById(id);
		if (pro.isPresent()) {
			new File("myApp/"+pro.get().getImageURL()).delete();
			productRepo.delete(pro.get());
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "Product id = "+id+" has been deleted!"));

		}

		return ResponseEntity.status(404).body(Map.of("message", "Product id = " + id + " not found"));

	}
}
