package config;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import controllermockup.CommentControllerMockup;
import controllermockup.CourseControllerMockup;
import controllermockup.CourseNotesControllerMockup;
import controllermockup.CourseNotesReferenceControllerMockup;
import controllermockup.LTIControllerMockup;
import controllermockup.UserControllerMockup;
import controllermockup.VideoControllerMockup;
import controllermockup.VideoReferenceControllerMockup;
import controllers.LTIController;
import exceptionhandler.ExceptionHandler;
import security.AuthenticationChecker;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"endpoint", "filter", "controller", "security"})
public class TestConfig extends AbstractConfig {
    
        private static final String HOME_URL = "http://student-dp8.intec.ugent.be/release";
        private static final String UPLOAD_LOCATION = "/var/www/classic/resources/courses";

	public TestConfig() {
                this.homeUrl = HOME_URL;
                this.pdfUploadLocation = UPLOAD_LOCATION;
        
		commentController = new CommentControllerMockup();
		videoController = new VideoControllerMockup();
		videoReferenceController = new VideoReferenceControllerMockup();
		courseNotesController = new CourseNotesControllerMockup();
		courseNotesReferenceController = new CourseNotesReferenceControllerMockup();
		courseController = new CourseControllerMockup();
		userController = new UserControllerMockup();
                ltiController = new LTIControllerMockup();
        
		this.exceptionHandler = new ExceptionHandler();
		this.authChecker = new AuthenticationChecker(userController);
	}

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
		factory.setPort(7000);
		factory.setSessionTimeout(10, TimeUnit.MINUTES);
		return factory;
	}
}
