package org.example.pokedexservice.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record PokemonDto(String id,
                         String pokemonId,
                         String nickname,
                         String pokemonName,
                         String pictureUrl,
                         Integer height,
                         Integer weight,
                         List<String> types) {
}
