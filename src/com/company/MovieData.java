package com.company;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author RJ Russell
 */

class MovieData {
    private String _id;
    private String imdb_id;
    private String title;
    private String year;
    private String content_rating;
    private String[] genre;
    private String[] stars;
    private String rating;
    private String length;
    private String description;
//    private String release_date;
//    private String original_title;
//    private String[] writers;
//    private String director;
//    private String rating_count;

// Additional fields in the JSON that are not wanted in the returned value.
//    private List<CastData> cast;
//    private URLData url;
//    private PosterData poster;
//    private List<TrailerData> trailer;
    MovieData() {}

    MovieData(String _id, String imdb_id, String title, String year,
              String content_rating, String[] genre, String[] stars,
              String rating, String length, String description) {
        this._id = _id;
        this.imdb_id = imdb_id;
        this.title = title;
        this.year = year;
        this.content_rating = content_rating;
        this.genre = genre;
        this.stars = stars;
        this.rating = rating;
        this.length = length;
        this.description = description;
    }

    @Override
    public String toString() {
        return  "ID: " + _id + "\n" +
                "IMDB Id: " + imdb_id + "\n" +
                "Title: " + title + "\n" +
                "Year: " + year + "\n" +
                "Content Rating: " + content_rating + "\n" +
                "Genre: " + getGenre() + "\n" +
                "Stars: " + getStars() + "\n" +
                "Rating: " + rating + "\n" +
                "Movie Length: " + length + "\n" +
                "Description: " + description + "\n";
    }

    String getImdb_id() {
        return imdb_id;
    }

    String getTitle() {
        return title;
    }

    String getYear() {
        return year;
    }

    String getContent_rating() {
        return content_rating;
    }

    String getGenre() {
        return Arrays.stream(genre)
                .map(String::toString)
                .collect(Collectors.joining(", "));
    }

    String getStars() {
        return Arrays.stream(stars)
                .map(String::toString)
                .collect(Collectors.joining(", "));
    }

    String getRating() {
        return rating;
    }

    String getLength() {
        return length;
    }

    String getDescription() {
        return description;
    }
}

// Additional classes that are included in the JSON and not wanted in return value.
//class URLData {
//    private String url;
//    private String title;
//    private String year;
//}
//class PosterData {
//    private String large;
//    private String thumb;
//}
//class CastData {
//    private String link;
//    private String image;
//    private String character;
//    private String name;
//}
//class TrailerData {
//    private String mimeType;
//    private String definition;
//    private String videoUrl;
//}
//
//class Error {
//    private String _type;
//    private String message;
//
//    @Override
//    public String toString() {
//        return message + "\n\n";
//    }
//}

