package org.example.pokedexservice.service;

import org.example.pokedexservice.dto.PokemonDto;
import org.example.pokedexservice.model.Pokemon;
import org.example.pokedexservice.model.PokemonType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class PokemonService {

    private final RestClient restClient;

    public PokemonService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://pokeapi.co/api/v2/pokemon")
                .build();
    }

    public PokemonDto getPokemon(String name) {
        Pokemon pokemon = restClient
                .get()
                .uri("/" + name)
                .retrieve()
                .body(Pokemon.class);

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

}
