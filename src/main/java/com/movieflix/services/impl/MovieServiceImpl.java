package com.movieflix.services.impl;

import com.movieflix.common.ApiUtils;
import com.movieflix.dto.MovieDto;
import com.movieflix.dto.MoviePageResponse;
import com.movieflix.entities.Movie;
import com.movieflix.exceptions.MovieNotFoundException;
import com.movieflix.repositories.MovieRepository;
import com.movieflix.services.FileService;
import com.movieflix.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
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
            throw new FileAlreadyExistsException("File already exists with name " + file.getOriginalFilename() + "!");
        }
        String uploadedFileName = fileService.uploadFile(path, file);
        movieDto.setPoster(uploadedFileName);
        Movie movie = apiUtils.mapToMovie(movieDto);
        Movie savedMovie = movieRepository.save(movie);
        return apiUtils.mapToMovieDto(savedMovie);
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found!"));
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
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found!"));
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
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found!"));
        movieRepository.delete(movie);
        Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));
        return "Movie deleted successfully!";
    }

    @Override
    public MoviePageResponse getAllMovieWithPagination(Integer pageNumber, Integer pageSize) {
        // 1. create Pageable object
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        // 2. Get the data from DB
        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();
        // 3. Convert to MovieDto object and return it
        List<MovieDto> movieDtoList = movies.stream()
                .map(apiUtils::mapToMovieDto)
                .collect(Collectors.toList());
        return new MoviePageResponse(movieDtoList,
                pageNumber,
                pageSize,
                moviePages.getTotalElements(),
                moviePages.getTotalPages(),
                moviePages.isLast());
    }

    @Override
    public MoviePageResponse getAllMovieWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String direction) {
        // 1. create Sort and Pageable object
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        // 2. Get the data from DB
        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();
        // 3. Convert to MovieDto object and return it
        List<MovieDto> movieDtoList = movies.stream()
                .map(apiUtils::mapToMovieDto)
                .collect(Collectors.toList());
        return new MoviePageResponse(movieDtoList,
                pageNumber,
                pageSize,
                moviePages.getTotalElements(),
                moviePages.getTotalPages(),
                moviePages.isLast());
    }
}
