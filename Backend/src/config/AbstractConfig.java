package config;

import interfaces.CommentControllerInterface;
import interfaces.CourseControllerInterface;
import interfaces.CourseNotesControllerInterface;
import interfaces.CourseNotesReferenceControllerInterface;
import interfaces.LTIControllerInterface;
import interfaces.UserControllerInterface;
import interfaces.VideoControllerInterface;
import interfaces.VideoReferenceControllerInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import security.AuthenticationChecker;
import exceptionhandler.ExceptionHandler;

/**
 * @author Jan
 */
@Configuration
@AutoConfigureBefore
public abstract class AbstractConfig {

	@Autowired
	protected ApplicationContext context;
	protected String pdfUploadLocation;
        protected String homeUrl;
        
	protected CommentControllerInterface commentController;
	protected VideoControllerInterface videoController;
    protected VideoReferenceControllerInterface videoReferenceController;
    protected CourseNotesControllerInterface courseNotesController;
    protected CourseNotesReferenceControllerInterface courseNotesReferenceController;
    protected UserControllerInterface userController;
    protected CourseControllerInterface courseController;
    protected ExceptionHandler exceptionHandler;
    protected AuthenticationChecker authChecker;
    protected LTIControllerInterface ltiController;
    
    @Bean
	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}
    
	@Bean
	public LTIControllerInterface getLTIController() {
		return ltiController;
	}
	
	@Bean
	public UserControllerInterface getUserController() {
		return userController;
	}
	
	@Bean
	public CommentControllerInterface getCommentController() {
		return commentController;
	}
	
	@Bean
	public VideoControllerInterface getVideoController() {
		return videoController;
	}
        
	@Bean
	public VideoReferenceControllerInterface getVideoReferenceController() {
		return videoReferenceController;
	}

	@Bean
	public CourseNotesControllerInterface getCourseNotesController() {
		return courseNotesController;
	}

	@Bean
	public CourseNotesReferenceControllerInterface getCourseNotesReferenceController() {
		return courseNotesReferenceController;
	}

	@Bean
	public CourseControllerInterface getCourseController() {
		return courseController;
	}

	public AuthenticationChecker getAuthenticationChecker() {
		return authChecker;
	}
        
        @Bean
	public String getPdfUploadLocation() {
		return pdfUploadLocation;
	}
        
        @Bean
	public String getHomeUrl() {
		return homeUrl;
	}
	
}