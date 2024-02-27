package com.wiredbraincoffee.productapiannotation.controller;

import com.wiredbraincoffee.productapiannotation.model.Product;
import com.wiredbraincoffee.productapiannotation.model.ProductEvent;
import com.wiredbraincoffee.productapiannotation.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/products")
public class ProductController {
    private ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    Flux<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /*
        @GetMapping("{id}")
        Mono<Product> getProduct(@PathVariable String id){
            return productRepository.findById(id);
        }
    */
    // Return a status code as well converting the Mono into ResponseEntity
    // with ResponseEntity.ok(body) in this case of Product
    @GetMapping("{id}")
    public Mono<ResponseEntity<Product>> getProduct(@PathVariable(value = "id") String id) {
        return productRepository.findById(id)
                .map(product -> ResponseEntity.ok(product))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Read the body of the request
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> saveProduct(@RequestBody Product product){
        return productRepository.save(product);
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Product>> updateProduct(@PathVariable(value = "id") String id,
                                                       @RequestBody Product product){
        return productRepository.findById(id)
                .flatMap(productFromDB -> {
                    productFromDB.setName(product.getName());
                    productFromDB.setPrice(product.getPrice());
                    return productRepository.save(productFromDB);
                })
                .map(updatedProduct -> ResponseEntity.ok(updatedProduct))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable(value = "id") String id){
        return productRepository.findById(id)
                .flatMap(
                        productFromDB ->
                                productRepository.delete(productFromDB)
                                        .then(Mono.just(ResponseEntity.ok().<Void>build()))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public Mono<Void> deleteAllProducts(){
        return productRepository.deleteAll();
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductEvent> getProductEvents(){
        return Flux.interval(Duration.ofSeconds(1))
                .map(value -> new ProductEvent(value, "Product Event"));
    }
}