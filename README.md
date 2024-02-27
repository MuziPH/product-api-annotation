Building a REST API with Annotated Controllers.
Non-blocking/asynchronous model
Reactive Spring Data

Links:
Spring WebFlux documentation for Annotated Controllers
https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-controller

Spring Data documentation for Reactive MongoDB repositories
https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo.reactive.repositories
Or
https://docs.spring.io/spring-data/mongodb/docs/3.2.2/reference/html/#mongo.reactive.repositories

Spring Boot: ApplicationRunner and CommandLineRunner
https://dzone.com/articles/spring-boot-applicationrunner-and-commandlinerunne

Using server-sent events
https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events



cURL commands
curl -v http://localhost:8080/products
curl -v http://localhost:8080/products/[ID]
curl -v -H "Content-Type: application/json" -d "{\"name\":\"Black Tea\", \"price\":1.99}" http://localhost:8080/products/
curl -v -H "Content-Type: application/json" -d "{\"name\":\"Black Tea\", \"price\":1.99}" -X PUT http://localhost:8080/products/[ID]
curl -v -X DELETE http://localhost:8080/products/[ID]
curl -v -X DELETE http://localhost:8080/products/
