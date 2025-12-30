package com.acme.w3e7.web.internal.portlet.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PokedexClient {
    private static final String API_URL = "https://pokeapi.co/api/v2/pokemon/";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public PokedexClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public JsonNode getPokemonData(String query) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + query.toLowerCase()))
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Pokémon não encontrado");
        }

        return objectMapper.readTree(response.body());
    }
}
