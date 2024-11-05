package com.example.information_systems_lab1.service;

import com.example.information_systems_lab1.entity.Movie;
import com.example.information_systems_lab1.repository.MovieRepository;
import com.example.information_systems_lab1.repository.PersonRepository;
import com.example.information_systems_lab1.request.MovieRequest;
import com.example.information_systems_lab1.validator.PersonValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final PersonService personService;


    @Transactional
    public void addMovie(MovieRequest movieRequest) throws Exception {
//       // TODO сделать эксепшены чтобы было КРАСИВА
//        TODO ИНКАПСУЛЯЦИЯ
        var direction = movieRequest.getDirector();
        if (direction == null) {
            if (movieRequest.getOperator_id() == null) {
                throw new Exception("direction не указан");
            }
            direction = personService.getPersonById(movieRequest.getDirector_id());

        }


        var screenwriter = movieRequest.getScreenwriter();
        if (screenwriter == null) {
            if (movieRequest.getScreenwriter_id() != null) {
                screenwriter =personService.getPersonById(movieRequest.getScreenwriter_id());
            }
        }

        var operator = movieRequest.getOperator();
        if (operator == null) {
            if (movieRequest.getOperator_id() == null) {
                throw new Exception("operator не указан");
            }
            operator = personService.getPersonById(movieRequest.getOperator_id());
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
        movieRepository.save(movie);
    }
}
