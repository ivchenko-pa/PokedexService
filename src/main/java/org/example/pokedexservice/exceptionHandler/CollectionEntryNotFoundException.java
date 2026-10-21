package org.example.pokedexservice.exceptionHandler;


public class CollectionEntryNotFoundException extends RuntimeException{

    public CollectionEntryNotFoundException(){}
    public CollectionEntryNotFoundException(String message) {
        super(message);
    }
}
