package com.qa.SweetSpot.controller;


import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;
import javax.validation.Valid;


import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.qa.SweetSpot.exception.ResourceNotFoundException;
import com.qa.SweetSpot.mySpringBootDatabaseApp.Users;
import com.qa.SweetSpot.repository.*;
import com.qa.SweetSpot.mySpringBootDatabaseApp.*;
import org.springframework.web.bind.annotation.CrossOrigin;


@CrossOrigin
@RestController
@RequestMapping("/api")
public class SpringBootController {

	
	private static final Logger logger = LoggerFactory.getLogger(SpringBootController.class);
	
	
	private static final ResponseEntity<?> ResponseEntitiy = null;

	@Autowired
	personRepository personRepo;
	
	@Autowired
	postRepository postRepo;
	
	@Autowired
	commentRepository commentRepo; 	
	

	
	public void saveFileToDb(MultipartFile multipart, Long postId) throws IOException {
	    File convFile = new File("C:\\Users\\Admin\\Documents\\JForum\\Forum-back-master\\images\\" + postId + ".jpg");
	    FileOutputStream fos = new FileOutputStream(convFile); 
	    fos.write(multipart.getBytes());
	    fos.close();
	}
	
	
	@PostMapping("/posts/{postId}/upload")
	public void singleFileUpload(@PathVariable(value = "postId")Long postId, @RequestParam("file") MultipartFile multipart) throws IOException {
		saveFileToDb(multipart, postId);
	}
	
	@RequestMapping(value = "/Image/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public String getImage(@PathVariable("id") String id) throws IOException {
		byte[] image = null;
		File folder = new File("C:\\Users\\Admin\\Documents\\JForum\\Forum-back-master\\images\\");
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
		    if (file.getName().equals(id + ".jpg")) {
		    	byte[] fileContent = Files.readAllBytes(file.toPath());
		    	String s = Base64.getEncoder().encodeToString(fileContent);
		    	return s;
		    }

		    }
		return "none";

	}
	//Method to create a person
	@PostMapping("/person")
	public Users createPerson(@Valid @RequestBody Users mSDM) {
		return personRepo.save(mSDM);
	}
	
	//Get a person by ID
	@GetMapping("/person/{id}")
	public Users getPersonByID(@PathVariable(value = "id")Long personID) {
		return personRepo.findById(personID).orElseThrow(()-> new ResourceNotFoundException("mySpringBootDataModel", "id", personID));
	}
	
	@RequestMapping("/person")
	public List<Users> getAllUsers() {
		return personRepo.findAll();
	}
	
	
	//Method to create a post
	@RequestMapping(value = "/posts", method = RequestMethod.POST)
	public Posts createPost(@Valid @RequestBody Posts pSDM) {
		pSDM.setUpvotes(0);
		return postRepo.save(pSDM);
	}
	
	@RequestMapping(value = "/posts", method = RequestMethod.GET)
	public List<Posts> getAllPosts() {
		return postRepo.findAll();
	}
	
	@RequestMapping(value="/posts/{id}", method=RequestMethod.DELETE)
	public Posts deletePerson(@PathVariable(value = "id")Long id) 
	{
		Posts pSDM = postRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("mySpringBootDataModel", "id", id));
		postRepo.delete(pSDM);
		return pSDM;
	}
	
	// Upvotes and Downvotes for Posts and Comments
	//////////////////////////////////////////////
	@RequestMapping(value="/posts/{id}/upvote", method=RequestMethod.PUT)
	public Posts upvotePost(@PathVariable(value = "id")Long id) 
	{
		Posts pSDM = postRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("mySpringBootDataModel", "id", id));
		pSDM.upvote();
		return postRepo.save(pSDM);
	}	
	
	@RequestMapping(value="/posts/{id}/downvote", method=RequestMethod.PUT)
	public Posts downvotePost(@PathVariable(value = "id")Long id) 
	{
		Posts pSDM = postRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("mySpringBootDataModel", "id", id));
		pSDM.downvote();
		return postRepo.save(pSDM);
	}	
	
	
	@RequestMapping(value="/comments/{id}/upvote", method=RequestMethod.PUT)
	public Comments upvoteComment(@PathVariable(value = "id") Long id)
	{
		Comments cSDM = commentRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("mySpringBootDataModel", "id", id));
		cSDM.upvote();
		return commentRepo.save(cSDM);
	}	
	
	@RequestMapping(value="/comments/{id}/downvote", method=RequestMethod.PUT)
	public Comments downvoteComment(@PathVariable(value = "id")Long id) 
	{
		Comments cSDM = commentRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("mySpringBootDataModel", "id", id));
		cSDM.downvote();
		return commentRepo.save(cSDM);
	}	
	
	
	@RequestMapping(value="/posts/{id}/upvote", method=RequestMethod.GET)
	public Posts getPostUpvote(@PathVariable(value = "id")Long id) 
	{
		Posts pSDM = postRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("mySpringBootDataModel", "id", id));;
		return pSDM;
	}
	
	@RequestMapping(value="/comments/{id}/upvotes", method=RequestMethod.GET)
	public Comments getCommentUpvote(@PathVariable(value = "id")Long id) 
	{
		Comments cSDM = commentRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("mySpringBootDataModel", "id", id));;
		return cSDM;
	}
	
	
	//////////////////////////////////////////////////
	
	//Creates a comment linked to a specific post
	@RequestMapping(value="/posts/{postId}/comments", method=RequestMethod.POST)
	public Comments createComment(@PathVariable (value="postId") Long postId, @Valid @RequestBody Comments comment) {
		return postRepo.findById(postId).map(posts -> {
			comment.setPost(posts);
			return commentRepo.save(comment);}).orElseThrow(()-> new ResourceNotFoundException("mySpringBootDataModel", "id", postId));
	}
	

	@RequestMapping(value="/posts/{postId}/comments", method=RequestMethod.GET)
	public Page<Comments> getAllCommentsByPostId(@PathVariable (value = "postId")Long postId, Pageable pageable){
		return commentRepo.findByPostId(postId,pageable);
	}
	
	@RequestMapping(value="/posts/{postId}/comments/{commentId}", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteComment(@PathVariable (value = "postId") long postId, @PathVariable (value = "commentId") long commentId)
	{
		if(!postRepo.existsById(postId)) {
			throw new ResourceNotFoundException("Person", "id", postId);
			}
		return commentRepo.findById(commentId).map(comment -> {
			commentRepo.delete(comment);
			return ResponseEntitiy.ok().build();
		}).orElseThrow(()-> new ResourceNotFoundException("mySpringBootDataModel", "id", commentId));
		}
	
	
	}
	

