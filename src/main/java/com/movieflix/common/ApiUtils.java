package com.movieflix.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieflix.dto.MovieDto;
import com.movieflix.entities.Movie;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApiUtils {

    @Value("${base.url}")
    private String baseUrl;

    public MovieDto mapToMovieDto(Movie movie) {
        String posterUrl = baseUrl + "/file/" + movie.getPoster();
        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                // convertStringToArray(savedMovie.getMovieCast()),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl);
    }

    public Movie mapToMovie(MovieDto movieDto) {
        Integer movieId = movieDto.getMovieId();
        if (movieId == null) {
            log.info("Adding movie");
            return new Movie(
                    null,
                    movieDto.getTitle(),
                    movieDto.getDirector(),
                    movieDto.getStudio(),
                    movieDto.getMovieCast(),
                    movieDto.getReleaseYear(),
                    movieDto.getPoster());
        } else {
            log.info("Updating movie");
            return new Movie(
                    movieDto.getMovieId(),
                    movieDto.getTitle(),
                    movieDto.getDirector(),
                    movieDto.getStudio(),
                    movieDto.getMovieCast(),
                    movieDto.getReleaseYear(),
                    movieDto.getPoster());
        }
    }

    // Convert String into JSON
    public MovieDto convertToMovieDto(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDtoObj, MovieDto.class);
    }

    /*
     * public static String convertArrayToString(String[] array) {
     * return String.join(",", array);
     * }
     * 
     * public static String[] convertStringToArray(String str) {
     * return str.split(",");
     * }
     */
}
