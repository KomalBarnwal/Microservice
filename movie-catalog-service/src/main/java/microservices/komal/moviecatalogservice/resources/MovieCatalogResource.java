package microservices.komal.moviecatalogservice.resources;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import microservices.komal.moviecatalogservice.models.CatalogItem;
import microservices.komal.moviecatalogservice.models.Movie;
import microservices.komal.moviecatalogservice.models.Rating;
import microservices.komal.moviecatalogservice.models.UserRating;


@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {
        
    	//get all rated movie IDs (copied rating.java form package microservices.komal.Ratingdataservice.model and
    	//pasted in package microservices.komal.moviecatalogservice.models)
    //	RestTemplate restTemplate = new RestTemplate();   (here we autowired the rest template so we dont have to write this line)
    //	Movie movie = restTemplate.getForObject("http://localhost:8082/movies/foo", Movie.class);
		
   // 	WebClient.Builder builder = WebClient.builder();   //here we create builder from that, whenever we call, we create a new client put it the parameters we need and send that     	
   /*	List<Rating> ratings = Arrays.asList(
    			new Rating("1234", 4),
    			new Rating("5678", 3)
    	);   */
    	
    	UserRating ratings = restTemplate.getForObject("http://rating-data-service/ratingsdata/users/" + userId, UserRating.class);
    	
    	return ratings.getUserRating().stream().map(rating -> {
    		//For each movie ID, call movie info service and get details  ////synchronous call
        	Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+ rating.getMovieId(), Movie.class);
        	//Put them all together
    		return new CatalogItem(movie.getName(), "Test Description", rating.getRating());
    	})
    	.collect(Collectors.toList());
    	
    	//return Collections.singletonList(new CatalogItem("Iron Man", "Test Description", 4));
    }
}

/*
     ////asynchronous call
Movie movie = webClientBuilder.build()
    .get()
    .uri("http://localhost:8082/movies/"+ rating.getMovieId())
    .retrieve()
    .bodyToMono(Movie.class)          //simply we are getting back an asynchronous object it is not exactly what we want but in future it will give what we want.Mono is a reactive way of saying you are getting an object back but not at that time, that will be in future sometime or a kind of promise that you'll get what u want.
    .block();                                 //this whole thing will give instance of a movie
    //if here is block, it is converting asynchronous to synchronous
*/