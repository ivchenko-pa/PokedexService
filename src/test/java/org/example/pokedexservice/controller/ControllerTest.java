package org.example.pokedexservice.controller;

import org.example.pokedexservice.dto.PokemonDto;
import org.example.pokedexservice.repository.Repository;
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

import java.util.List;

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

    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer mockServer = MockRestServiceServer.bindTo(builder).build();



    @Test
    void getPokemon_shouldReturnPokemon_whenValidRequested() throws Exception {

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

    @Test
    void addFavorite_shouldReturnPokemon_whenValidRequested() throws Exception {
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
        mockMvc.perform(MockMvcRequestBuilders.post("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "pokemonName": "clefairy",
                                    "nickname": "My Super Starter"
                                }
                                """))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                            "pokemonId": "35",
                            "nickname": "My Super Starter",
                            "pokemonName": "clefairy",
                            "pictureUrl": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/35.png",
                            "height": 6,
                            "weight": 75,
                            "types": [
                                "fairy"
                            ]
                        }
                        """))
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty());
    }

    @Test
    void addFavorite_shouldReturnBadRequestWithDetails_whenEmptyPokemonName() throws Exception {
        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "pokemonName": "",
                                    "nickname": "My Super Starter"
                                }
                                """))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                            "pokemonName": "Should not be empty"
                        }
                        """))
                .andExpect(MockMvcResultMatchers.jsonPath("timestamp").isNotEmpty());
    }

    @Test
    void addFavorite_shouldReturnBadRequestWithDetails_whenEmptyNickName() throws Exception {
        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "pokemonName": "kdkdkd",
                                    "nickname": ""
                                }
                                """))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                            "nickname": "Should be at least 2 characters long"
                        }
                        """))
                .andExpect(MockMvcResultMatchers.jsonPath("timestamp").isNotEmpty());
    }

    @Test
    void addFavorite_shouldReturnBadRequestWithDetails_whenEmptyPokemonNameAndOneCharacterNickName() throws Exception {
        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "pokemonName": "",
                                    "nickname": "A"
                                }
                                """))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("""
                            {
                            "pokemonName": "Should not be empty",
                            "nickname": "Should be at least 2 characters long"
                            }
                        """))
                .andExpect(MockMvcResultMatchers.jsonPath("timestamp").isNotEmpty());
    }

    @Test
    void getAllFavoritePokemons_shouldReturnList_whenOneExist() throws Exception {
        //GIVEN
        PokemonDto expectedPokemonDto = PokemonDto.builder()
                .id("123")
                .nickname("someNikname")
                .pokemonId("35")
                .pokemonName("clefairy")
                .pictureUrl("https://example.com/clefairy.png")
                .weight(75)
                .height(6)
                .types(List.of("fairy"))
                .build();
        repo.saveAll(List.of(expectedPokemonDto));
        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/collection"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                          [{
                            "id": "123",
                            "pokemonId": "35",
                            "nickname": "someNikname",
                            "pokemonName": "clefairy",
                            "pictureUrl": "https://example.com/clefairy.png",
                            "height": 6,
                            "weight": 75,
                            "types": [
                                "fairy"
                            ]
                        }]
                        """));
    }

    @Test
    void getAllFavoritePokemons_shouldReturnEmptyList_whenNoneExist() throws Exception {
        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/collection"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                          []
                        """));
    }
}