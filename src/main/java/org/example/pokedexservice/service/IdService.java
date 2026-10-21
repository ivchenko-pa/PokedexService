package org.example.pokedexservice.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdService {
    public String getId(){
        return UUID.randomUUID().toString();
    }
}
