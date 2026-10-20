package org.example.pokedexservice.controller;

import lombok.AllArgsConstructor;
import org.example.pokedexservice.dto.PokemonDto;
import org.example.pokedexservice.service.PokemonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pokemon")
@AllArgsConstructor
public class Controller {

    private final PokemonService pokemonService;

    @GetMapping("/{name}")
    public PokemonDto getPokemon(@PathVariable String name){
        return pokemonService.getPokemon(name);
    }

}
