package com.movieflix.services.impl;

import com.movieflix.common.ApiUtils;
import com.movieflix.dto.MovieDto;
import com.movieflix.entities.Movie;
import com.movieflix.exceptions.FileHandlingException;
import com.movieflix.exceptions.GlobleException;
import com.movieflix.repositories.MovieRepository;
import com.movieflix.services.FileService;
import com.movieflix.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final FileService fileService;
    private final ApiUtils apiUtils;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileHandlingException("File already exists with name " + file.getOriginalFilename() + "!");
        }
        String uploadedFileName = fileService.uploadFile(path, file);
        movieDto.setPoster(uploadedFileName);
        Movie movie = apiUtils.mapToMovie(movieDto);
        Movie savedMovie = movieRepository.save(movie);
        return apiUtils.mapToMovieDto(savedMovie);
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new GlobleException("Movie not found!"));
        return apiUtils.mapToMovieDto(movie);
    }

    @Override
    public List<MovieDto> getAllMovie() {
        List<Movie> movieList = movieRepository.findAll();
        return movieList.stream()
                .map(apiUtils::mapToMovieDto)
                .collect(Collectors.toList());
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new GlobleException("Movie not found!"));
        String fileName = movie.getPoster();
        if (file != null) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }
        movieDto.setPoster(fileName);
        Movie updatedMovie = apiUtils.mapToMovie(movieDto);
        Movie savedMovie = movieRepository.save(updatedMovie);
        return apiUtils.mapToMovieDto(savedMovie);
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new GlobleException("Movie not found!"));
        movieRepository.delete(movie);
        Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));
        return "Movie deleted successfully!";
    }
}
