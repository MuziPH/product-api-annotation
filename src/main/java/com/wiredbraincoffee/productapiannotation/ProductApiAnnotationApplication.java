package com.wiredbraincoffee.productapiannotation;

import com.wiredbraincoffee.productapiannotation.model.Product;
import com.wiredbraincoffee.productapiannotation.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class ProductApiAnnotationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApiAnnotationApplication.class, args);
	}
	@Bean
	CommandLineRunner init(ProductRepository productRepository, ReactiveMongoOperations operations){
		return args -> {
			Flux<Product> productFlux = Flux.just(
					new Product(null,"Big Latte",2.99),
					new Product(null,"Big Decaf",2.49),
					new Product(null,"Green Tea", 1.99)
			).flatMap(product -> productRepository.save(product));


			productFlux.thenMany(productRepository.findAll()).subscribe(System.out::println);

/*
			operations.collectionExists(Product.class)
					.flatMap(exists -> exists ? operations.dropCollection(Product.class): Mono.just(exists))
					.thenMany(v -> operations.createCollection(Product.class))
					.thenMany(productFlux)
					.thenMany(productRepository.findAll())
					.subscribe(System.out::println);
*/
		};
	}

}
