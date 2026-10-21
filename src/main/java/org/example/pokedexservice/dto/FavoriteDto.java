package org.example.pokedexservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record FavoriteDto(
        @NotEmpty(message = "Should not be empty")
        String pokemonName,
        @Pattern(regexp = "..+", message = "Should be at least 2 characters long")
        String nickname) {
}
