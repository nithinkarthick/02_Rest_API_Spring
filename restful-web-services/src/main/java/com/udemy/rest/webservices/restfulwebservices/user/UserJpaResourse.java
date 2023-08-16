package com.udemy.rest.webservices.restfulwebservices.user;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.udemy.rest.webservices.restfulwebservices.jpa.PostRepository;
import com.udemy.rest.webservices.restfulwebservices.jpa.UserRepository;

import jakarta.validation.Valid;

@RestController
public class UserJpaResourse {

	
	private UserRepository repository;
	
	private PostRepository postRepository;
	

	public UserJpaResourse(UserRepository repository, PostRepository postRepository) {
		this.repository = repository;
		this.postRepository =postRepository;
		
	}

	// GET users
	@GetMapping("/jpa/users")
	public List<User> retrieveAllUsers(){

		return repository.findAll();

	}

	//
	// GET users
	@GetMapping("/jpa/users/{id}")
	public EntityModel<User> specificUsers(@PathVariable int id){

		Optional<User> response =  repository.findById(id);
		if(response.isEmpty()) 
			throw new userNotFoundException("id:"+id);
		
		EntityModel<User> entityModel = EntityModel.of(response.get());
		
		WebMvcLinkBuilder link =  linkTo(methodOn(this.getClass()).retrieveAllUsers());
		entityModel.add(link.withRel("all-users"));
		
		return entityModel;

	}

	//POST /users
	@PostMapping("/jpa/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		User response = repository.save(user);
		
		URI Location= ServletUriComponentsBuilder.fromCurrentRequest()
												.path("/{id}")
												.buildAndExpand(response.getId())
												.toUri();
		return ResponseEntity.created(Location).build();
	}


	// DELETE users
		@DeleteMapping("/jpa/users/{id}")
		public void deleteUser(@PathVariable int id){

			repository.deleteById(id);

		}
		
		@GetMapping("/jpa/users/{id}/post")
		public List<Post> retrievePostForUser(@PathVariable int id){


			Optional<User> response =  repository.findById(id);
			if(response.isEmpty()) 
				throw new userNotFoundException("id:"+id);

			return response.get().getPost();
		}
		



		
		@PostMapping("/jpa/users/{id}/posts")
		public ResponseEntity<Object> createPostForUser(@PathVariable int id, @Valid @RequestBody Post post) {
			Optional<User> user = repository.findById(id);
			
			if(user.isEmpty())
				throw new userNotFoundException("id:"+id);
			
			post.setUser(user.get());
			
			Post savedPost = postRepository.save(post);
			
			URI location = ServletUriComponentsBuilder.fromCurrentRequest()
					.path("/{id}")
					.buildAndExpand(savedPost.getId())
					.toUri();   

			return ResponseEntity.created(location).build();

		}
		
}
