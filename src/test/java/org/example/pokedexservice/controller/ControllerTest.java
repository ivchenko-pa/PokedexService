package org.example.pokedexservice.controller;

import org.example.pokedexservice.repository.Repository;
import org.example.pokedexservice.service.IdService;
import org.example.pokedexservice.service.PokemonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Repository repo;
    @Autowired
    private IdService idService;

    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer mockServer = MockRestServiceServer.bindTo(builder).build();
    private final PokemonService pokemonService = new PokemonService(builder, repo, idService);



    @Test
    void getPokemon_shouldRetournPokemon_whenValidRequested() throws Exception {

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

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/pokemon/clefairy"))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                            "id": null,
                            "pokemonId": "35",
                            "nickname": null,
                            "pokemonName": "clefairy",
                            "pictureUrl": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/35.png",
                            "height": 6,
                            "weight": 75,
                            "types": [
                                "fairy"
                            ]
                        }
                        """));

    }

    @Test
    void getPokemon_shouldReturnBadRequestStatus_whenPokemonNotExistOnExternalApi() throws Exception {
        //GIVEN
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/SomeNotValidPokemon"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/pokemon/SomeNotValidPokemon"))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Pokemon with name: SomeNotValidPokemon not found!"));
    }
}