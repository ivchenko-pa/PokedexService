package org.example.pokedexservice.service;


import org.example.pokedexservice.dto.FavoriteDto;
import org.example.pokedexservice.dto.PokemonDto;
import org.example.pokedexservice.exceptionHandler.CollectionEntryNotFoundException;
import org.example.pokedexservice.exceptionHandler.PokemonNotFoundException;
import org.example.pokedexservice.repository.Repository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        } catch (PokemonNotFoundException e) {
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

    @Test
    void getAllFavoritePokemons_shouldBeListWithOne_whenOneExist() {
        //GIVEN
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

        List<PokemonDto> expectedList = List.of(expectedPokemonDto);

        //WHEN
        when(mockRepo.findAll()).thenReturn(List.of(expectedPokemonDto));
        List<PokemonDto> actualList = pokemonService.getAllFavoritePokemons();

        //THEN
        assertEquals(expectedList, actualList);
        verify(mockRepo, times(1)).findAll();
    }

    @Test
    void getAllFavoritePokemons_shouldBeEmptyList_whenNothingInDatabase() {
        //GIVEN
        List<PokemonDto> expectedList = Collections.emptyList();

        //WHEN
        when(mockRepo.findAll()).thenReturn(Collections.emptyList());
        List<PokemonDto> actualList = pokemonService.getAllFavoritePokemons();

        //THEN
        assertEquals(expectedList, actualList);
        verify(mockRepo, times(1)).findAll();
    }

    @Test
    void getFavoritePokemon_shouldBeOnePokemon_WhenOneExist() {
        //GIVEN
        PokemonDto expectedPokemon = PokemonDto.builder()
                .id("123")
                .nickname("My favorite")
                .pokemonId("35")
                .pokemonName("clefairy")
                .pictureUrl("https://example.com/clefairy.png")
                .weight(75)
                .height(6)
                .types(List.of("fairy"))
                .build();
        //WHEN
        when(mockRepo.findById("123")).thenReturn(Optional.of(expectedPokemon));
        PokemonDto actualPokemon = pokemonService.getFavoritePokemon("123");

        //THEN
        assertEquals(expectedPokemon, actualPokemon);
        verify(mockRepo, times(1)).findById("123");
    }

    @Test
    void getFavoritePokemon_shouldThrowException_whenNotExist() {
        //GIVEN
        when(mockRepo.findById("someId")).thenReturn(Optional.empty());

        //WHEN
        try {
            pokemonService.getFavoritePokemon("someId");
        //THEN
            fail("Expected Exception not thrown, even though pokemon with id: someId, that does not exist.");
        } catch (CollectionEntryNotFoundException e) {
            var expectedMessage = "Favorite Pokemon with id: someId not found!";
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void deleteFavoritePokemon_shouldBeTrue_whenOneExist() {
        //GIVEN
        when(mockRepo.existsById("123")).thenReturn(true);

        //WHEN
        boolean actualResult = pokemonService.deleteFavoritePokemon("123");

        //THEN
        assertEquals(true, actualResult);
        verify(mockRepo, times(1)).existsById("123");
        verify(mockRepo, times(1)).deleteById("123");
    }

    @Test
    void deleteFavoritePokemon_shouldBeFalse_whenNoneExist() {
        //GIVEN
        when(mockRepo.existsById("123")).thenReturn(false);

        //WHEN
        try {
            pokemonService.deleteFavoritePokemon("123");
        //THEN
            fail("Expected Exception not thrown, even though pokemon with id: 123, that does not exist.");
        } catch (CollectionEntryNotFoundException e) {
            var expectedMessage = "Favorite Pokemon with id: 123 not found!";
            assertEquals(expectedMessage, e.getMessage());
            verify(mockRepo, times(1)).existsById("123");
            verify(mockRepo, times(0)).deleteById("123");
        }
    }

    @Test
    void updateFavoritePokemonById_shouldBeReturnedOne_whenOneExists() {
        //GIVEN
        FavoriteDto inputFavoriteDto = new FavoriteDto(null, "New nickname");
        PokemonDto outdatedPokemon = PokemonDto.builder()
                .id("123")
                .nickname("Old nickname")
                .pokemonId("35")
                .pokemonName("clefairy")
                .pictureUrl("https://example.com/clefairy.png")
                .weight(75)
                .height(6)
                .types(List.of("fairy"))
                .build();
        PokemonDto expectedPokemon = PokemonDto.builder()
                .id("123")
                .nickname("New nickname")
                .pokemonId("35")
                .pokemonName("clefairy")
                .pictureUrl("https://example.com/clefairy.png")
                .weight(75)
                .height(6)
                .types(List.of("fairy"))
                .build();

        when(mockRepo.findById("123")).thenReturn(Optional.of(outdatedPokemon));
        when(mockRepo.save(expectedPokemon)).thenReturn(expectedPokemon);

        //WHEN
        PokemonDto actualPokemon = pokemonService.updateFavoritePokemon("123", inputFavoriteDto);

        //THEN
        assertEquals(expectedPokemon, actualPokemon);
        verify(mockRepo, times(1)).save(actualPokemon);
    }

}