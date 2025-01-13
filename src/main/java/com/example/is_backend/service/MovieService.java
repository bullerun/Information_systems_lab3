package com.example.is_backend.service;

import com.example.is_backend.authentication.service.UserServices;
import com.example.is_backend.dto.MovieDTO;
import com.example.is_backend.entity.Coordinates;
import com.example.is_backend.entity.Movie;
import com.example.is_backend.entity.Person;
import com.example.is_backend.exception.InsufficientEditingRightsException;
import com.example.is_backend.exception.MovieNotFoundException;
import com.example.is_backend.exception.NotFoundException;
import com.example.is_backend.exception.PersonValidationException;
import com.example.is_backend.repository.MovieRepository;
import com.example.is_backend.request.MovieRequest;
import com.example.is_backend.validator.MovieValidator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final PersonService personService;
    private final MovieValidator movieValidator;
    private final UserServices userService;
    private final Map<String, Object> movieCache = new ConcurrentHashMap<>();
    private static final Object DUMMY_VALUE = new Object();


    private String generateMovieKey(String name, Integer length) {
        return name + "::" + length;
    }


    @PostConstruct
    private void loadMoviesToCache() {
        movieRepository.findAll().forEach(movie -> movieCache.put(generateMovieKey(movie.getName(), movie.getLength()), DUMMY_VALUE));
    }

    @Transactional
    public void addMovie(MovieRequest movieRequest) throws NotFoundException, PersonValidationException, InsufficientEditingRightsException, IllegalArgumentException {
        String movieKey = generateMovieKey(movieRequest.getName(), movieRequest.getLength());


        if (movieCache.containsKey(movieKey)) {
            throw new IllegalArgumentException("фильм уже создан!");
        }

        var userId = userService.getCurrentUserId();


        var director = getOrCreatePerson(movieRequest.getDirector(), movieRequest.getDirector_id(), "director не указан", userId);
        var screenwriter = getOrCreatePerson(movieRequest.getScreenwriter(), movieRequest.getScreenwriter_id(), null, userId); // screenwriter может быть null
        var operator = getOrCreatePerson(movieRequest.getOperator(), movieRequest.getOperator_id(), "operator не указан", userId);


        personService.validateDirectionScreenwriterOperator(director, screenwriter, operator);

        Movie movie = buildMovie(movieRequest, userId, director, screenwriter, operator);

        if (movieCache.containsKey(movieKey)) {
            return;
        }
        movieCache.put(movieKey, DUMMY_VALUE);
        try {
            if (director.getId() == null) {
                movie.setDirector(personService.addPerson(director));
            }
            if (screenwriter != null && screenwriter.getId() == null) {
                movie.setScreenwriter(personService.addPerson(screenwriter));
            }
            if (operator.getId() == null) {
                movie.setOperator(personService.addPerson(operator));
            }
            movieRepository.save(movie);
        } catch (Exception e) {
            movieCache.remove(movieKey);
            throw e;
        }
    }

    private Person getOrCreatePerson(Person person, Long personId, String notFoundMessage, Long ownerId) throws NotFoundException {
        if (person != null) {
            person.setOwnerId(ownerId);
            return person;
        }
        if (personId == null) {
            if (notFoundMessage != null) {
                throw new NotFoundException(notFoundMessage);
            }
            return null;
        }
        return personService.getPersonById(personId);
    }

    private Movie buildMovie(MovieRequest movieRequest, Long userId, Person director, Person screenwriter, Person operator) {
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
        movie.setDirector(director);
        movie.setScreenwriter(screenwriter);
        movie.setOperator(operator);
        movie.setOwnerId(userId);
        return movie;
    }

    @Transactional
    public void update(Long id, MovieRequest updatedMovie) throws NotFoundException, InsufficientEditingRightsException, MovieNotFoundException {
        if (movieCache.containsKey(generateMovieKey(updatedMovie.getName(), updatedMovie.getLength()))) {
            throw new InsufficientEditingRightsException("Такой фильм уже есть");
        }

        var movie = movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException("а чет не нашлось фильма то"));
        if (!movie.getOwnerId().equals(userService.getCurrentUserId())) {
            throw new InsufficientEditingRightsException("недостаточно прав на изменение фильма");
        }

        movieCache.put(generateMovieKey(updatedMovie.getName(), updatedMovie.getLength()), DUMMY_VALUE);

        try {
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
        } catch (Exception e) {
            movieCache.remove(generateMovieKey(updatedMovie.getName(), updatedMovie.getLength()));
            throw e;
        }

    }

    public void selectHowUpdatePerson(Movie movie, MovieRequest updatedMovie) throws InsufficientEditingRightsException, NotFoundException {
        if (updatedMovie.getDirector_id() != null) {
            movie.setDirector(personService.getPersonById(updatedMovie.getDirector_id()));
        } else if (updatedMovie.getDirector() != null) {
            personService.updatePersonFields(movie.getDirector(), updatedMovie.getDirector(), "direction");
        }

        if (updatedMovie.getScreenwriter_id() != null) {
            movie.setScreenwriter(personService.getPersonById(updatedMovie.getScreenwriter_id()));

        } else if (movie.getScreenwriter() == null) {
            movie.setScreenwriter(updatedMovie.getScreenwriter());
            movie.getScreenwriter().setOwnerId(userService.getCurrentUserId());

        } else if (updatedMovie.getScreenwriter() != null) {
            personService.updatePersonFields(movie.getScreenwriter(), updatedMovie.getScreenwriter(), "screenwriter");
        } else {
            movie.setScreenwriter(null);
        }


        if (updatedMovie.getOperator_id() != null) {
            movie.setOperator(personService.getPersonById(updatedMovie.getOperator_id()));
        } else if (updatedMovie.getOperator() != null) {
            personService.updatePersonFields(movie.getOperator(), updatedMovie.getOperator(), "operator");
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

    public boolean validateMovie(MovieRequest entity) {
        movieValidator.validateMovie(entity);
        return true;
    }

    @Transactional
    public void addMovies(ArrayList<MovieRequest> movies) throws PersonValidationException, NotFoundException {
        for (MovieRequest movie : movies) {
            if (validateMovie(movie)) {
                try {
                    addMovie(movie);
                } catch (IllegalArgumentException | InsufficientEditingRightsException _) {
                }
            }
        }
    }
}
