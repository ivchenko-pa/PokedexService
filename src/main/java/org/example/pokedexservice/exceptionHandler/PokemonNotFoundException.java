package org.example.pokedexservice.exceptionHandler;


public class PokemonNotFoundException extends RuntimeException{

    public PokemonNotFoundException(){}
    public PokemonNotFoundException(String message) {
        super(message);
    }
}
