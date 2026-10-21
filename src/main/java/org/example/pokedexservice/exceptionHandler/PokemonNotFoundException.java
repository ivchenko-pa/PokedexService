package org.example.pokedexservice.exceptionHandler;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

public class PokemonNotFoundException extends RuntimeException{

    public PokemonNotFoundException(){}
    public PokemonNotFoundException(String message) {
        super(message);
    }
}
