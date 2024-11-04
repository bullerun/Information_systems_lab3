package com.example.information_systems_lab1.service;

import com.example.information_systems_lab1.entity.Movie;
import com.example.information_systems_lab1.repository.MovieRepository;
import com.example.information_systems_lab1.repository.PersonRepository;
import com.example.information_systems_lab1.request.MovieRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final PersonRepository personRepository;

    @Transactional
    public void addMovie(MovieRequest movieRequest) throws Exception {
//       // TODO сделать эксепшены чтобы было КРАСИВА
        var direction = movieRequest.getDirector();
        if (direction == null) {
            if (movieRequest.getDirector_id() == null) {
                throw new Exception("презираю жабу");
            }
            direction = personRepository.findById(movieRequest.getDirector_id())
                    .orElseThrow(() -> new RuntimeException("презираю жабу"));
        }


        var screenwriter = movieRequest.getScreenwriter();
        if (screenwriter == null) {
            if (movieRequest.getScreenwriter_id() != null) {
                screenwriter = personRepository.findById(movieRequest.getScreenwriter_id())
                        .orElseThrow(() -> new RuntimeException("презираю жабу"));
            }
        }

        var operator = movieRequest.getOperator();
        if (operator == null) {
            if (movieRequest.getOperator_id() == null) {
                throw new Exception("презираю жабу");
            }
            operator = personRepository.findById(movieRequest.getOperator_id())
                    .orElseThrow(() -> new RuntimeException("презираю жабу"));
        }

        //TODO нужно как-то проверить правильность Person
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
