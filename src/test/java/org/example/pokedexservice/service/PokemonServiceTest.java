package org.example.pokedexservice.service;

import lombok.AllArgsConstructor;
import org.example.pokedexservice.dto.PokemonDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureRestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


class PokemonServiceTest {

    private RestClient.Builder builder = RestClient.builder();

    private MockRestServiceServer mockServer = MockRestServiceServer.bindTo(builder).build();

    private PokemonService pokemonService = new PokemonService(builder);


    @Test
    void getPokemon() {
        //GIVEN
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/clefairy"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                         {
                                  "id": 35,
                                  "name": "clefairy",
                                  "height": 6,
                                  "weight": 75,
                                  "types": [
                                    {
                                      "type": {
                                        "name": "fairy"
                                      }
                                    }
                                  ],
                                  "sprites": {
                                    "other": {
                                      "official-artwork": {
                                        "front_default": "https://example.com/clefairy.png"
                                      }
                                    }
                                  }
                                }
                        """, MediaType.APPLICATION_JSON));
        PokemonDto expectedPokemonDto = PokemonDto.builder()
                .pokemonId("35")
                .pokemonName("clefairy")
                .pictureUrl("https://example.com/clefairy.png")
                .weight(75)
                .height(6)
                .types(List.of("fairy"))
                .build();
        //WHEN
        PokemonDto actualPokemonDto = pokemonService.getPokemon("clefairy");
        //THEN
        assertEquals(expectedPokemonDto, actualPokemonDto);
    }
}