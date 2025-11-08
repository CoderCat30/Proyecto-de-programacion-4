package com.tienda.app.controller;

import com.tienda.app.model.Pokemon;
import com.tienda.app.service.PokeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class PokeController {

    private final PokeService pokeService;

    public PokeController(PokeService pokeService) {
        this.pokeService = pokeService;
    }

    @GetMapping("/")
    public String mostrarPokedex(Model model) {
        List<Pokemon> pokemons = pokeService.obtenerPokemones("55");
        model.addAttribute("pokemons", pokemons);
        return "index";
    }

    @GetMapping("/pokedex/{limit}")
    public String mostrarPokedex(@PathVariable("limit") String limit, Model model) {
        List<Pokemon> pokemons = pokeService.obtenerPokemones(limit);
        model.addAttribute("pokemons", pokemons);
        return "index";
    }

    @GetMapping("/api/pokemon/+{id}")
    public String mostrarPokemon(Model model, @PathVariable String id) {
        model.addAttribute("pokemon", pokeService.obtenerPokemon(id));
        return "index";
    }
}
