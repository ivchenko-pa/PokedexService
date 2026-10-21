package org.example.pokedexservice.dto;

import lombok.Builder;
import lombok.With;
import org.springframework.data.annotation.Id;

import java.util.List;

@Builder
public record PokemonDto(@With @Id String id,
                         String pokemonId,
                         @With String nickname,
                         String pokemonName,
                         String pictureUrl,
                         Integer height,
                         Integer weight,
                         List<String> types) {
}
