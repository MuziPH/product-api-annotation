package com.wiredbraincoffee.productapiannotation;

import com.wiredbraincoffee.productapiannotation.controller.ProductController;
import com.wiredbraincoffee.productapiannotation.model.Product;
import com.wiredbraincoffee.productapiannotation.model.ProductEvent;
import com.wiredbraincoffee.productapiannotation.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@WebFluxTest(ProductController.class)
public class JUnit5WebFluxAnnotationTest {
    @Autowired
    private WebTestClient webTestClient;
    private List<Product> expectedList;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private CommandLineRunner commandLineRunner;

    @BeforeEach
    void beforeEach() {
        expectedList = Arrays.asList(new Product("1", "Big Latte", 2.99));
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Flux.fromIterable(expectedList));

        webTestClient.get()
                .uri("/products")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Product.class)
                .isEqualTo(expectedList);
    }

    @Test
    void testProductIdFound() {
        Product expectedProduct = expectedList.get(0);
        when(productRepository.findById(expectedProduct.getId())).thenReturn(Mono.just(expectedProduct));

        webTestClient.get()
                .uri("/products/{id}", expectedProduct.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Product.class)
                .isEqualTo(expectedProduct);
    }

    @Test
    void testProductInvalidNotFound() {
        String pathId = "/aaa";
        when(productRepository.findById(pathId)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/products/{pathId}", pathId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testProductEvents() {
        ProductEvent expectedEvent = new ProductEvent(0L, "Product Event");

        FluxExchangeResult<ProductEvent> result = webTestClient.get()
                .uri("/products/events")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductEvent.class);

        StepVerifier.create(result.getResponseBody())
                .expectNext(expectedEvent) // compare with expectedEvent
                .expectNextCount(2) // pass two events
                .consumeNextWith(nextEvent -> assertEquals(Long.valueOf(3), nextEvent.getEventId()))
                .thenCancel()
                .verify();
    }

}
