package com.wiredbraincoffee.productapiannotation;

import com.wiredbraincoffee.productapiannotation.model.Product;
import com.wiredbraincoffee.productapiannotation.model.ProductEvent;
import com.wiredbraincoffee.productapiannotation.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JUnit5ApplicationContextTest {
    private WebTestClient webTestClient;
    private List<Product> expectedList;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void beforeEach() {
        this.webTestClient = WebTestClient
                .bindToApplicationContext(applicationContext)
                .configureClient()
                .baseUrl("/products")
                .build();

        // Collect the results of the Flux into expectedList and
        // block until the completion of filling the list with all items from the Flux
        expectedList = productRepository.findAll().collectList().block();
    }

    @Test
    void testGetAllProducts() {
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
        webTestClient.get()
                .uri("/aaa")
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
