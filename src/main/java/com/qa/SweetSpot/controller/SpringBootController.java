package com.qa.SweetSpot.controller;


import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
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
import com.qa.SweetSpot.repository.*;
import com.qa.SweetSpot.mySpringBootDatabaseApp.*;
import org.springframework.web.bind.annotation.CrossOrigin;


@CrossOrigin
@RestController
@RequestMapping("/api")
public class SpringBootController {

	
	private static final Logger logger = LoggerFactory.getLogger(SpringBootController.class);
	private String extension = "";
	private static final ResponseEntity<?> ResponseEntitiy = null;

	@Autowired
	personRepository personRepo;
	
	@Autowired
	postRepository postRepo;
	
	@Autowired
	commentRepository commentRepo; 	
	

	
	public void saveFileToDb(MultipartFile multipart, Long postId, String extension) throws IOException {
	    File convFile = new File("C:\\Users\\Admin\\Documents\\JForum\\Forum-back-master\\images\\" + postId + "." + extension);
	    FileOutputStream fos = new FileOutputStream(convFile); 
	    fos.write(multipart.getBytes());
	    fos.close();
	}
	
	
	@PostMapping("/posts/{postId}/upload")
	public String singleFileUpload(@PathVariable(value = "postId")Long postId, @RequestParam("file") MultipartFile multipart) throws IOException {
		extension = FilenameUtils.getExtension(multipart.getOriginalFilename());
		System.out.println(extension);
		saveFileToDb(multipart, postId, extension);
		return extension;
	}
	

	@RequestMapping(value = "/getFile/{id}", method = RequestMethod.GET)
	public String getFile(@PathVariable("id") String id) throws IOException {
		byte[] image = null;
		String str = "";
		File folder = new File("C:\\Users\\Admin\\Documents\\JForum\\Forum-back-master\\images\\");
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
	    	str = file.getName().substring(0, file.getName().length()-4);
	    	extension = file.getName().substring(file.getName().length()-3, file.getName().length());
		    if (str.equals(id)) {
	
		    	return extension;
		    }

		    }
		return "none";
	}
	

	@RequestMapping(value = "/gif/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_GIF_VALUE)
	public String getGif(@PathVariable("id") String id) throws IOException {
		byte[] image = null;
		File folder = new File("C:\\Users\\Admin\\Documents\\JForum\\Forum-back-master\\images\\");
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
		    if (file.getName().equals(id + ".gif")) {
		    	byte[] fileContent = Files.readAllBytes(file.toPath());
		    	String s = Base64.getEncoder().encodeToString(fileContent);
		    	return s;
		    }
		    }
		return "none";
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
	
	@RequestMapping(value = "/mp4/{id}", method = RequestMethod.GET)
	public String getMp4(@PathVariable("id") String id) throws IOException {
		byte[] image = null;
		File folder = new File("C:\\Users\\Admin\\Documents\\JForum\\Forum-back-master\\images\\");
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
		    if (file.getName().equals(id + ".mp4")) {
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
	
	
	//Method to create a person
	@PostMapping("/person/login")
	public String getPerson(@Valid @RequestBody Users mSDM) {
		try {
			System.out.println(mSDM.getUsername());
			Users user = personRepo.findByusername(mSDM.getUsername());
			if(user.getPassword().equals(mSDM.getPassword())) {
				return "true";
			}
			else 
			{
				return "false";
			}
		}
		catch (NullPointerException e) {
			return "false";
		}
	}
	

	
	@GetMapping("/person")
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
	
	@SuppressWarnings("unlikely-arg-type")
	@RequestMapping(value="/posts/{id}", method=RequestMethod.DELETE)
	public Posts deletePerson(@PathVariable(value = "id")Long id) 
	{
		String str = "";
		String Tid = id + "";
		Posts pSDM = postRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("mySpringBootDataModel", "id", id));
		postRepo.delete(pSDM);
		File folder = new File("C:\\Users\\Admin\\Documents\\JForum\\Forum-back-master\\images\\");
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
	    	str = file.getName().substring(0, file.getName().length()-4);
		    if (str.equals(Tid)) {
		    	file.delete();
		    }
		}
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
	public List<Comments> deleteComment(@PathVariable (value = "postId") long postId, @PathVariable (value = "commentId") long commentId)
	{
		if(!postRepo.existsById(postId)) {
			throw new ResourceNotFoundException("Person", "id", postId);
			}
		return commentRepo.findById(commentId).map(comment -> {
			commentRepo.delete(comment);
			List<Comments> allComs= commentRepo.findAll();
			return allComs;
		}).orElseThrow(()-> new ResourceNotFoundException("mySpringBootDataModel", "id", commentId));
			
			
		}
	
	@RequestMapping(value = "/sortPostsByUpvotes", method = RequestMethod.GET)
	public List<Posts> sortAllPostsByUpvotes() {
		List<Posts> posts = postRepo.findAll();
		Posts[] postArr = (Posts[]) posts.toArray(new Posts[posts.size()]);
        Boolean swapp;
        int n = postArr.length-1;
        do {
            swapp = false;
            for(int i = 0; i < n; i++){
              if(postArr[i].getUpvotes() < postArr[i+1].getUpvotes()){
            	  Posts temp = postArr[i];
                  postArr[i] = postArr[i +1];
                  postArr[i+1] = temp;
                  swapp = true;
              }
            }
            n--;
        }while(swapp);
        posts = Arrays.asList(postArr);
		return posts;
	}
	
	@RequestMapping(value = "/sortByDateAscending", method = RequestMethod.GET)
	public List<Posts> sortByDateAscending(){
		List<Posts> posts = postRepo.findAll();
		Posts[] postArr = (Posts[]) posts.toArray(new Posts[posts.size()]);
        Boolean swapp;
        int n = postArr.length-1;
        do {
            swapp = false;
            for(int i = 0; i < n; i++){
              if(postArr[i].getDate().compareTo(postArr[i+1].getDate()) < 0){
            	  Posts temp = postArr[i];
                  postArr[i] = postArr[i +1];
                  postArr[i+1] = temp;
                  swapp = true;
              }
            }
            n--;
        }while(swapp);
        posts = Arrays.asList(postArr);
		return posts;
	}
	
	
	@RequestMapping(value = "/{postId}/sortByDateAscending", method = RequestMethod.GET)
	public List<Comments> sortCommentsByDateAscending(@PathVariable (value = "postId")Long postId, Pageable pageable){
		Page<Comments> comment = commentRepo.findByPostId(postId, pageable);
		List<Comments> comments = comment.getContent();
		Comments[] commentsArr = (Comments[]) comments.toArray(new Comments[comments.size()]);
        Boolean swapp;
        int n = commentsArr.length-1;
        do {
            swapp = false;
            for(int i = 0; i < n; i++){
              if(commentsArr[i].getCreatedDate().compareTo(commentsArr[i+1].getCreatedDate()) < 0){
            	  Comments temp = commentsArr[i];
                  commentsArr[i] = commentsArr[i +1];
                  commentsArr[i+1] = temp;
                  swapp = true;
              }
            }
            n--;
        }while(swapp);
        comments = Arrays.asList(commentsArr);
		return comments;
	}
	
	@RequestMapping(value = "/{postId}/sortCommentsByUpvotes", method = RequestMethod.GET)
	public List<Comments> sortAllCommentsByUpvotes(@PathVariable (value = "postId")Long postId, Pageable pageable) {
		Page<Comments> comment = commentRepo.findByPostId(postId, pageable);
		List<Comments> comments = comment.getContent();
		Comments[] commentArr = (Comments[]) comments.toArray(new Comments[comments.size()]);
        Boolean swapp;
        int n = commentArr.length-1;
        do {
            swapp = false;
            for(int i = 0; i < n; i++){
              if(commentArr[i].getUpvotes() < commentArr[i+1].getUpvotes()){
            	  Comments temp = commentArr[i];
                  commentArr[i] = commentArr[i +1];
                  commentArr[i+1] = temp;
                  swapp = true;
              }
            }
            n--;
        }while(swapp);
        comments = Arrays.asList(commentArr);
		return comments;
	}
	
	
}
	

