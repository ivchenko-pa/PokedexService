package org.example.pokedexservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.pokedexservice.dto.FavoriteDto;
import org.example.pokedexservice.dto.PokemonDto;
import org.example.pokedexservice.service.PokemonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.List;

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
    public PokemonDto addFavorite(@RequestBody @Valid FavoriteDto favoriteDto){
        return pokemonService.addFavorite(favoriteDto);
    }

    @GetMapping("/collection")
    public List<PokemonDto> getAllFavoritePokemons(){
        return pokemonService.getAllFavoritePokemons();
    }

    @GetMapping("/collection/{id}")
    public PokemonDto getFavoritePokemon(@PathVariable String id){
        return pokemonService.getFavoritePokemon(id);
    }

    @DeleteMapping("/collection/{id}")
    public boolean deleteFavoritePokemon(@PathVariable String id){
        return pokemonService.deleteFavoritePokemon(id);
    }

    @PutMapping("collection/{id}")
    public PokemonDto updateFavoritePokemon(@PathVariable String id, @RequestBody FavoriteDto favoriteDto){
        return pokemonService.updateFavoritePokemon(id, favoriteDto);
    }
}
