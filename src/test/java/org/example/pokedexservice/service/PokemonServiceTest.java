package org.example.pokedexservice.service;

import lombok.AllArgsConstructor;
import org.example.pokedexservice.dto.FavoriteDto;
import org.example.pokedexservice.dto.PokemonDto;
import org.example.pokedexservice.exceptionHandler.PokemonNotFoundException;
import org.example.pokedexservice.repository.Repository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureRestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;


class PokemonServiceTest {

    private RestClient.Builder builder = RestClient.builder();

    private MockRestServiceServer mockServer = MockRestServiceServer.bindTo(builder).build();
    Repository mockRepo = mock(Repository.class);
    IdService mockIdService = mock(IdService.class);
    private PokemonService pokemonService = new PokemonService(builder, mockRepo, mockIdService);


    @Test
    void getPokemon_shouldReturnValidDto_whenExist() {
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
                .id(null)
                .nickname(null)
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

    @Test
    void getPokemon_shouldThrowException_whenNotExist() {
        //GIVEN
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/someName"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        //WHEN
        try {
            pokemonService.getPokemon("someName");
        //THEN
            fail("Expected Exception not thrown, even though pokemon with name: someName, that does not exist.");
        }catch(PokemonNotFoundException e){
            var expectedMessage = "Pokemon with name: someName not found!";
            assertEquals(expectedMessage, e.getMessage());
        }
    }


    @Test
    void addFavorite_shouldBeReturnedPokemonDto_whenValidFavotiteDto() {
        //GIVEN
        FavoriteDto favoriteDto = new FavoriteDto("clefairy", "My favorite");
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
        when(mockIdService.getId()).thenReturn("123");
        PokemonDto actualPokemonDto = pokemonService.addFavorite(favoriteDto);

        //THEN
        PokemonDto expectedPokemonDto = PokemonDto.builder()
                .id("123")
                .nickname("My favorite")
                .pokemonId("35")
                .pokemonName("clefairy")
                .pictureUrl("https://example.com/clefairy.png")
                .weight(75)
                .height(6)
                .types(List.of("fairy"))
                .build();

        assertEquals(expectedPokemonDto, actualPokemonDto);
        verify(mockRepo, times(1)).save(actualPokemonDto);
    }
}