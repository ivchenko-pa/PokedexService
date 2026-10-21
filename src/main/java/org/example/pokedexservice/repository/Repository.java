package org.example.pokedexservice.repository;

import org.example.pokedexservice.dto.PokemonDto;
import org.springframework.data.mongodb.repository.MongoRepository;

@org.springframework.stereotype.Repository
public interface Repository extends MongoRepository<PokemonDto, String> {
}
