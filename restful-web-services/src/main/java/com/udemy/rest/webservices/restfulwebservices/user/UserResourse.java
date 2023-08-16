package com.udemy.rest.webservices.restfulwebservices.user;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.URI;
import java.util.List;

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

import jakarta.validation.Valid;

@RestController
public class UserResourse {

	private UserDoaService service;

	public UserResourse(UserDoaService service) {
		this.service = service;
	}

	// GET users
	@GetMapping("/users")
	public List<User> retrieveAllUsers(){

		return service.findAll();

	}

	//
	// GET users
	@GetMapping("/users/{id}")
	public EntityModel<User> specificUsers(@PathVariable int id){

		User response =  service.findOne(id);
		if(response==null) 
			throw new userNotFoundException("id:"+id);
		
		EntityModel<User> entityModel = EntityModel.of(response);
		
		WebMvcLinkBuilder link =  linkTo(methodOn(this.getClass()).retrieveAllUsers());
		entityModel.add(link.withRel("all-users"));
		
		return entityModel;

	}

	//POST /users
	@PostMapping("/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		User response = service.save(user);
		
		URI Location= ServletUriComponentsBuilder.fromCurrentRequest()
												.path("/{id}")
												.buildAndExpand(response.getId())
												.toUri();
		return ResponseEntity.created(Location).build();
	}


	// DELETE users
		@DeleteMapping("/users/{id}")
		public void deleteUser(@PathVariable int id){

			  service.deleteById(id);

		}
		

}
