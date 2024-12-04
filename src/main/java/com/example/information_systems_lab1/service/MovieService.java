package com.example.information_systems_lab1.service;

import com.example.information_systems_lab1.authentication.service.UserServices;
import com.example.information_systems_lab1.dto.MovieDTO;
import com.example.information_systems_lab1.entity.Coordinates;
import com.example.information_systems_lab1.entity.Movie;
import com.example.information_systems_lab1.exception.*;
import com.example.information_systems_lab1.repository.MovieRepository;
import com.example.information_systems_lab1.request.MovieRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final PersonService personService;
    private final UserServices userService;

    @Transactional
    public void addMovie(MovieRequest movieRequest) throws NotFoundException, PersonValidationException {
        // TODO сделать эксепшены чтобы было КРАСИВА
        var userID = userService.getCurrentUserId();
        var direction = movieRequest.getDirector();
        if (direction == null) {
            if (movieRequest.getDirector_id() == null) {
                throw new NotFoundException("director не указан");
            }
            direction = personService.getPersonById(movieRequest.getDirector_id());
        } else {
            direction.setOwnerId(userID);
        }

        var screenwriter = movieRequest.getScreenwriter();
        if (screenwriter == null) {
            if (movieRequest.getScreenwriter_id() != null) {
                screenwriter = personService.getPersonById(movieRequest.getScreenwriter_id());
            }
        } else {
            screenwriter.setOwnerId(userID);
        }

        var operator = movieRequest.getOperator();
        if (operator == null) {
            if (movieRequest.getOperator_id() == null) {
                throw new NotFoundException("operator не указан");
            }
            operator = personService.getPersonById(movieRequest.getOperator_id());
        } else {
            operator.setOwnerId(userID);
        }

        personService.validateDirectionScreenwriterOperator(direction, screenwriter, operator);
        Movie movie = new Movie();
        movie.setName(movieRequest.getName());
        movie.setCoordinates(movieRequest.getCoordinates());
        movie.setOscarsCount(movieRequest.getOscarsCount());
        movie.setBudget(movieRequest.getBudget());
        movie.setTotalBoxOffice(movieRequest.getTotalBoxOffice());
        movie.setMpaaRating(movieRequest.getMpaaRating());
        movie.setLength(movieRequest.getLength());
        movie.setGoldenPalmCount(movieRequest.getGoldenPalmCount());
        movie.setGenre(movieRequest.getGenre());
        movie.setDirector(direction);
        movie.setScreenwriter(screenwriter);
        movie.setOperator(operator);
        movie.setOwnerId(userID);
        movieRepository.save(movie);
    }

    @Transactional
    public void update(Long id, MovieRequest updatedMovie) throws NotFoundException, InsufficientEditingRightsException, MovieNotFoundException {
        var movie = movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException("а чет не нашлось фильма то"));
        if (!movie.getOwnerId().equals(userService.getCurrentUserId())) {
            throw new InsufficientEditingRightsException("недостаточно прав на изменение фильма");
        }
        movie.setName(updatedMovie.getName());
        if (!movie.getCoordinates().equals(updatedMovie.getCoordinates())) {
            updateCoordinates(movie.getCoordinates(), updatedMovie.getCoordinates());
        }
        selectHowUpdatePerson(movie, updatedMovie);
        movie.setOscarsCount(updatedMovie.getOscarsCount());
        movie.setBudget(updatedMovie.getBudget());
        movie.setTotalBoxOffice(updatedMovie.getTotalBoxOffice());
        movie.setMpaaRating(updatedMovie.getMpaaRating());
        movie.setLength(updatedMovie.getLength());
        movie.setGenre(updatedMovie.getGenre());
        movieRepository.save(movie);
    }

    public void selectHowUpdatePerson(Movie movie, MovieRequest updatedMovie) throws InsufficientEditingRightsException, NotFoundException {
        if (updatedMovie.getDirector_id() != null) {
            movie.setDirector(personService.getPersonById(updatedMovie.getDirector_id()));
        } else if (updatedMovie.getDirector() != null) {
            personService.updatePerson(movie.getDirector(), updatedMovie.getDirector(), "direction");
        }

        if (updatedMovie.getScreenwriter_id() != null) {
            movie.setScreenwriter(personService.getPersonById(updatedMovie.getScreenwriter_id()));

        } else if (movie.getScreenwriter() == null) {
            movie.setScreenwriter(updatedMovie.getScreenwriter());
            movie.getScreenwriter().setOwnerId(userService.getCurrentUserId());

        } else if (updatedMovie.getScreenwriter() != null) {
            personService.updatePerson(movie.getScreenwriter(), updatedMovie.getScreenwriter(), "screenwriter");
        } else {
            movie.setScreenwriter(null);
        }


        if (updatedMovie.getOperator_id() != null) {
            movie.setOperator(personService.getPersonById(updatedMovie.getOperator_id()));
        } else if (updatedMovie.getOperator() != null) {
            personService.updatePerson(movie.getOperator(), updatedMovie.getOperator(), "operator");
        }

    }

    public void updateCoordinates(Coordinates oldCoordinates, Coordinates newCoordinates) {
        oldCoordinates.setX(newCoordinates.getX());
        oldCoordinates.setY(newCoordinates.getY());
    }

    public List<MovieDTO> getAllMovies(Integer page, Integer pageSize, String sortDirection, String sortProperty) {
        Page<Movie> a = movieRepository.findAll(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortProperty)));
        return toMovieDTO(a.getContent());
    }

    private List<MovieDTO> toMovieDTO(List<Movie> content) {
        List<MovieDTO> dtos = new ArrayList<>();
        for (Movie movie : content) {
            dtos.add(toMovieDTO(movie));
        }
        return dtos;
    }

    private MovieDTO toMovieDTO(Movie movie) {
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setName(movie.getName());
        dto.setCoordinates(movie.getCoordinates());
        dto.setCreationDate(movie.getCreationDate());
        dto.setOscarsCount(movie.getOscarsCount());
        dto.setBudget(movie.getBudget());
        dto.setTotalBoxOffice(movie.getTotalBoxOffice());
        dto.setMpaaRating(movie.getMpaaRating());
        dto.setDirector(personService.toPersonDTO(movie.getDirector()));
        if (movie.getScreenwriter() != null) {
            dto.setScreenwriter(personService.toPersonDTO(movie.getScreenwriter()));
        }
        dto.setOperator(personService.toPersonDTO(movie.getOperator()));
        dto.setLength(movie.getLength());
        dto.setGenre(movie.getGenre());
        dto.setGoldenPalmCount(movie.getGoldenPalmCount());
        dto.setOwner_id(movie.getOwnerId());
        return dto;
    }
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
}
