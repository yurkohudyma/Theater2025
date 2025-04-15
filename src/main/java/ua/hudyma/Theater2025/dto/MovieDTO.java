package ua.hudyma.Theater2025.dto;

import ua.hudyma.Theater2025.constants.Genre;
import ua.hudyma.Theater2025.model.Movie;

import java.time.LocalDate;

public record MovieDTO(
        Long id,
        Genre genre,
        LocalDate premiereStart,
        LocalDate showEnd,
        String imdbIndex,
        String name,
        String imgUrl
) {

    public static MovieDTO from (Movie movie){
        return new MovieDTO(
                movie.getId(),
                movie.getGenre(),
                movie.getPremiereStart(),
                movie.getShowEnd(),
                movie.getImdbIndex(),
                movie.getName(),
                movie.getImgUrl());
    }
}
