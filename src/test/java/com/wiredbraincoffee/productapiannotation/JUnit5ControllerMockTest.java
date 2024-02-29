package com.wiredbraincoffee.productapiannotation;

import com.wiredbraincoffee.productapiannotation.controller.ProductController;
import com.wiredbraincoffee.productapiannotation.model.Product;
import com.wiredbraincoffee.productapiannotation.model.ProductEvent;
import com.wiredbraincoffee.productapiannotation.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ExtendWith(SpringExtension.class)
public class JUnit5ControllerMockTest {
    private WebTestClient webTestClient;
    private List<Product> expectedList;
    @MockBean
    private ProductRepository productRepository;

    @BeforeEach
    void beforeEach() {

        this.webTestClient = WebTestClient
                .bindToController(new ProductController(productRepository))
                .configureClient()
                .baseUrl("/products")
                .build();

        expectedList = Arrays.asList(new Product("1", "Big Latte", 2.99));
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Flux.fromIterable(expectedList));

        webTestClient.get()
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
                .uri("/{id}", expectedProduct.getId())
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
                .uri("/{pathId}", pathId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testProductEvents() {
        ProductEvent expectedEvent = new ProductEvent(0L, "Product Event");

        FluxExchangeResult<ProductEvent> result = webTestClient.get()
                .uri("/events")
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
