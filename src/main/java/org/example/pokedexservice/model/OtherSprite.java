package org.example.pokedexservice.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public record OtherSprite(@JsonProperty("official-artwork") OfficialArtwork officialArtwork) {
}
