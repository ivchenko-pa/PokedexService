package org.example.pokedexservice.model;

import java.util.List;

public record Pokemon(Integer id,
                      String name,
                      Integer height,
                      Integer weight,
                      Sprites sprites,
                      List<PokemonType> types) {
}
