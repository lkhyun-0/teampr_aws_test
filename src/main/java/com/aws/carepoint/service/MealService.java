package com.aws.carepoint.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class MealService {
    private final WebClient webClient;

    public MealService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8000").build();
    }

    public Mono<String> getMealRecommendation(String goal) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/recommend")
                        .queryParam("goal", goal)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}
