package org.example.pokedexservice.controller;

import lombok.AllArgsConstructor;
import org.example.pokedexservice.dto.FavoriteDto;
import org.example.pokedexservice.dto.PokemonDto;
import org.example.pokedexservice.service.PokemonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class Controller {

    private final PokemonService pokemonService;

    @GetMapping("/pokemon/{name}")
    public PokemonDto getPokemon(@PathVariable String name){
        return pokemonService.getPokemon(name);
    }

    @PostMapping("/collection")
    public PokemonDto addFavorite(@RequestBody FavoriteDto favoriteDto){
        return pokemonService.addFavorite(favoriteDto);
    }


}
