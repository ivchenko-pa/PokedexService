package org.example.pokedexservice.service;

import org.example.pokedexservice.dto.FavoriteDto;
import org.example.pokedexservice.dto.PokemonDto;
import org.example.pokedexservice.exceptionHandler.PokemonNotFoundException;
import org.example.pokedexservice.model.Pokemon;
import org.example.pokedexservice.model.PokemonType;
import org.example.pokedexservice.repository.Repository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class PokemonService {

    private final RestClient restClient;
    private final Repository repo;
    private final IdService idService;

    public PokemonService(RestClient.Builder restClientBuilder, Repository repo, IdService idService) {

        this.restClient = restClientBuilder
                .baseUrl("https://pokeapi.co/api/v2/pokemon")
                .build();
        this.repo = repo;
        this.idService = idService;
    }

    public PokemonDto getPokemon(String name) {
        Pokemon pokemon;
        try {
            pokemon = restClient
                    .get()
                    .uri("/" + name)
                    .retrieve()
                    .body(Pokemon.class);
        } catch (HttpClientErrorException e){
            throw new PokemonNotFoundException(String.format("Pokemon with name: %s not found!", name));
        }
        List<String> pokemonDtoTypes = new ArrayList<>();
        for (PokemonType pokemonType : pokemon.types()) {
            pokemonDtoTypes.add(pokemonType.type().name());
        }
        return PokemonDto.builder()
                .pokemonId(pokemon.id().toString())
                .pokemonName(pokemon.name())
                .pictureUrl(pokemon.sprites().other().officialArtwork().frontDefault())
                .height(pokemon.height())
                .weight(pokemon.weight())
                .types(pokemonDtoTypes)
                .build();
    }

    public PokemonDto addFavorite(FavoriteDto favoriteDto){
        PokemonDto pokemonDto = getPokemon(favoriteDto.pokemonName())
                .withId(idService.getId())
                .withNickname(favoriteDto.nickname());
        repo.save(pokemonDto);
        return pokemonDto;
    }

}
