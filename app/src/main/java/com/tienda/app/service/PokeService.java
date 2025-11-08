package com.tienda.app.service;

import com.tienda.app.model.Pokemon;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PokeService {

    private static final String API_URL = "https://pokeapi.co/api/v2/pokemon?limit=";

    public List<Pokemon> obtenerPokemones(String Limit) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> response = restTemplate.getForObject(API_URL+Limit, Map.class);
        List<Map<String, String>> results = (List<Map<String, String>>) response.get("results");

        List<Pokemon> pokemons = new ArrayList<>();

        for (Map<String, String> result : results) {
            String name = result.get("name");
            String url = result.get("url");

            // obtenemos detalles individuales del Pokémon
            Map<String, Object> detalle = restTemplate.getForObject(url, Map.class);

            Pokemon p = new Pokemon();
            p.setName(name);
            p.setId((Integer) detalle.get("id"));
            p.setHeight((Integer) detalle.get("height"));
            p.setWeight((Integer) detalle.get("weight"));

            Map<String, Object> sprites = (Map<String, Object>) detalle.get("sprites");
            p.setImage((String) sprites.get("front_default"));

            pokemons.add(p);
        }

        return pokemons;
    }
    public Pokemon obtenerPokemon(String id) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(API_URL+id, Map.class);
        Pokemon p = new Pokemon();
        p.setId((Integer) response.get("id"));
        p.setHeight((Integer) response.get("height"));
        p.setWeight((Integer) response.get("weight"));
        return p;
    }
}