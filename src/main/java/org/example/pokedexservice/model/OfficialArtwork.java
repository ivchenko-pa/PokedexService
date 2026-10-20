package org.example.pokedexservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OfficialArtwork(@JsonProperty ("front_default") String frontDefault) {
}
