package com.example.mk2_electricbungaloo;

import java.net.MalformedURLException;
import java.net.URL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Random;

public class Movie {

    private static final String apiKey = "telacreistewey";

    // Movie attributes
    int movieId;
    String movieTitle;
    String posterPath;

    // Constructor (when you declare a movie object, this is what constructs it)
    public Movie(int id, String title){
        this.movieId = id;
        this.movieTitle = title;

    }

    // Method that checks if the movie is available on a specific provider, in a specific region
    public boolean isAvailable(String watchProvider, String region){

        try {

            // Creates a url
            URL url = new URL("https://api.themoviedb.org/3/movie/" + this.movieId + "?api_key=" + apiKey + "&language=en-US&append_to_response=watch%2Fproviders&watch_region=" + region);
            // Calls the method to fetch the data and stores in a string
            String urlResult = JsonRequest.requestInfo(url);

            // Makes the string an object
            Object object = new JSONParser().parse(urlResult);
            // Make the object a JSONObject
            JSONObject jsonObject = (JSONObject) object;

            // Going through the messy nested objects
            JSONObject watchProviders = (JSONObject) jsonObject.get("watch/providers");
            JSONObject results = (JSONObject) watchProviders.get("results");
            JSONObject watchRegion = (JSONObject) results.get(region);
            JSONArray flatrate = (JSONArray) watchRegion.get("flatrate");

            // Loops through the array, looking for the provider
            for(int i = 0; i < flatrate.size(); i++) {

                JSONObject provider = (JSONObject) flatrate.get(i);

                if(provider.get("provider_id").toString().equals(watchProvider)){
                    return true;
                }
            }

        } catch (MalformedURLException | ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            System.out.println("Error (isAvailable): Couldn't find info in object (most probably flatrate).");
        }


        return false;
    }

    // Method that gets the movie info from the id and returns a movie object
    static Movie getMovie(int id){

        // Creates a url
        URL url = null;
        try {
            url = new URL("https://api.themoviedb.org/3/movie/" + id + "?api_key=" + apiKey + "&language=en-US");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String urlResult = JsonRequest.requestInfo(url);

        // Makes the string an object
        Object object = null;
        try {
            object = new JSONParser().parse(urlResult);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Make the object a JSONObject
        JSONObject jsonObject = (JSONObject) object;

        try{
            String originalTitle = jsonObject.get("original_title").toString();
            Movie movie = new Movie(id, originalTitle);
            movie.posterPath = jsonObject.get("poster_path").toString();
            return movie;

        }catch (NullPointerException e){
            System.out.println("Error: Movie does not exist.");
            Movie errorMovie = new Movie(0, "Error");
            return errorMovie;
        }

    }

    // Method that returns a random movie
    static Movie randomMovie(String watchProvider, String region){
        while (true) {

            Random random = new Random(); // Instance of random class
            int randomId = random.nextInt(65000); // Generates random number

            Movie movie = getMovie(randomId);

            if (movie.isAvailable(watchProvider, region)){
                return movie;
            }

        }
    }

    // Method that uses the TMDB discovery feature to get specified amount of popular movies, returning an array of them
    static ArrayList<Movie> getPopulars(int amountMovies, String watchProvider, String region){

        ArrayList<Movie> popularMovies = new ArrayList<>(amountMovies);

        URL url = null;
        try {
            url = new URL("https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&with_watch_providers=" + watchProvider + "&watch_region=" + region + "&with_watch_monetization_types=flatrate");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String urlResult = JsonRequest.requestInfo(url);

        // Makes the string an object
        Object object = null;
        try {
            object = new JSONParser().parse(urlResult);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Make the object a JSONObject
        JSONObject jsonObject = (JSONObject) object;

        // Surround with try just in case
        try {
            JSONArray results = (JSONArray) jsonObject.get("results");

            // Loops through the JSONArray and adds the movies to the movie array
            for (int i = 0; i < amountMovies; i++){
                JSONObject movieJson = (JSONObject) results.get(i);
                int movieId = Integer.parseInt(movieJson.get("id").toString());
                String movieTitle = movieJson.get("original_title").toString();
                Movie movie = new Movie(movieId, movieTitle);
                movie.posterPath = movieJson.get("poster_path").toString();
                popularMovies.add(movie);
            }
        }catch (NullPointerException e){
            System.out.println("Error (getPopulars): Couldn't find info while looping through array.");
        }

        return popularMovies;
    }

    // Uses TMDB Get Similar to return an array of similar movies available in specified region and provider
    public ArrayList<Movie> getSimilar(int amountMovies, String watchProvider, String region){

        // ArrayList to be returned with size of specified amount of movies
        ArrayList<Movie> similarMovies = new ArrayList<>(amountMovies);

        URL url = null;
        String urlResult = null;
        try {
            url = new URL("https://api.themoviedb.org/3/movie/"+ this.movieId +"/similar?api_key=" + apiKey + "&language=en-US&page=1");
            urlResult = JsonRequest.requestInfo(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            System.out.println("Error: Strange encounter with JSON.");
            return new ArrayList<>();
        }

        // Makes the string an object
        Object object = null;
        try {
            object = new JSONParser().parse(urlResult);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Make the object a JSONObject
        JSONObject jsonObject = (JSONObject) object;

        // Accessing data
        try{
            JSONArray results = (JSONArray) jsonObject.get("results");

            for(int i = 0; i < results.size(); i++){
                JSONObject movieJson = (JSONObject) results.get(i);
                int movieId = Integer.parseInt(movieJson.get("id").toString());
                Movie movie = getMovie(movieId);
                if (movie.isAvailable(watchProvider, region)){
                    if(similarMovies.size() == amountMovies){
                        break;
                    }
                    similarMovies.add(movie);
                }
            }

        }catch (NullPointerException e){
            System.out.println("Error (getSimilar): NullPointer while looping through array.");
            return new ArrayList<>();
        }

        return similarMovies;
    }

    // Return the top movie from results of a query from TMDB
    static Movie getTopSearchResult(String query, String watchProvider, String region){

        query = query.replace(" ", "%20");
        URL url = null;
        String urlResult = null;
        try {
            url = new URL("https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&language=en-US&query=" + query + "&page=1&include_adult=false");
            urlResult = JsonRequest.requestInfo(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            System.out.println("Error: Unable to find results from query.");
            return new Movie(0, "Error");
        }

        // Makes the string an object
        Object object = null;
        try {
            object = new JSONParser().parse(urlResult);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Make the object a JSONObject
        JSONObject jsonObject = (JSONObject) object;

        try{

            JSONArray results = (JSONArray) jsonObject.get("results");

            for(int i=0; i < results.size(); i++) {

                JSONObject movie = (JSONObject) results.get(i);

                int topMovieId = Integer.parseInt(movie.get("id").toString());
                String topMovieTitle = movie.get("original_title").toString();
                String topMoviePosterPath = movie.get("poster_path").toString();

                Movie topMovie = new Movie(topMovieId, topMovieTitle);
                topMovie.posterPath = topMoviePosterPath;

                if(topMovie.isAvailable(watchProvider, region)){
                    return topMovie;
                }
            }

        }catch (NullPointerException e){
            System.out.println("Error: Unable to find results from query.");
            return new Movie(0, "Error");
        }

        System.out.println("Error: Movie not available in region.");
        return new Movie(0, "Error");
    }

    @Override
    public String toString(){
        return this.movieTitle + " - " + this.movieId;
    }

    @Override
    public boolean equals(Object o){

        // If the movie is compared with itself
        if (o == this){
            return true;
        }

        // Check if the object is an instance the movie class
        if (!(o instanceof Movie)){
            return false;
        }

        // Typecasting to movie to compare data
        Movie movie = (Movie) o;

        return Integer.compare(this.movieId, movie.movieId) == 0;

    }

}
