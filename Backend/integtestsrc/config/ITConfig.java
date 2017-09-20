package config;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import controllers.CommentController;
import controllers.CourseController;
import controllers.CourseNotesController;
import controllers.CourseNotesReferenceController;
import controllers.LTIController;
import controllers.UserController;
import controllers.VideoController;
import controllers.VideoReferenceController;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.implementation.CommentDao;
import databaseaccess.implementation.CourseDao;
import databaseaccess.implementation.CourseNotesDao;
import databaseaccess.implementation.CourseNotesReferenceDao;
import databaseaccess.implementation.LectureDao;
import databaseaccess.implementation.LtiDao;
import databaseaccess.implementation.ReplyDao;
import databaseaccess.implementation.UserDao;
import databaseaccess.implementation.VideoDao;
import databaseaccess.implementation.VideoReferenceDao;
import exceptionhandler.ExceptionHandler;
import security.AuthenticationChecker;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"endpoint", "filter", "controller", "security"})
public class ITConfig extends AbstractConfig {

    private static final String DATABASE_CONFIG = "testdbconfig.properties";
    private static final String UPLOAD_LOCATION = "/var/www/classic/resources/courses";
    private static final String HOME_URL = "http://student-dp8.intec.ugent.be/release";

    public ITConfig() {
        this.pdfUploadLocation = UPLOAD_LOCATION;
        this.homeUrl = HOME_URL;
        VideoDao videoDao = null;
        CourseNotesDao courseNotesDao = null;
        CommentDao commentDao = null;
        ReplyDao replyDao = null;
        VideoReferenceDao videoRefDao = null;
        CourseNotesReferenceDao courseNotesRefDao = null;
        UserDao userDao = null;
        CourseDao courseDao = null;
        LectureDao lectureDao = null;
        LtiDao ltiDao = null;
        try {
            videoDao = new VideoDao(DATABASE_CONFIG);
            commentDao = new CommentDao(DATABASE_CONFIG);
            replyDao = new ReplyDao(DATABASE_CONFIG);
            videoRefDao = new VideoReferenceDao(DATABASE_CONFIG);
            courseNotesRefDao = new CourseNotesReferenceDao(DATABASE_CONFIG);
            courseNotesDao = new CourseNotesDao(DATABASE_CONFIG);
            userDao = new UserDao(DATABASE_CONFIG);
            courseDao = new CourseDao(DATABASE_CONFIG);
            lectureDao = new LectureDao(DATABASE_CONFIG);
            ltiDao = new LtiDao(DATABASE_CONFIG);
        } catch (ClassicDatabaseException ex) {
            Logger.getLogger(ITConfig.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.commentController = new CommentController(commentDao, replyDao, videoRefDao, courseNotesRefDao, userDao);
        this.videoController = new VideoController(videoDao, courseDao, userDao);
        this.videoReferenceController = new VideoReferenceController(videoRefDao, commentDao, userDao);
        this.courseNotesController = new CourseNotesController(courseNotesDao, courseDao, userDao, UPLOAD_LOCATION);
        this.courseNotesReferenceController = new CourseNotesReferenceController(courseNotesRefDao, commentDao, userDao);
        this.userController = new UserController(userDao);
        this.courseController = new CourseController(courseDao, lectureDao, videoDao, courseNotesDao, userDao, UPLOAD_LOCATION);
        this.ltiController = new LTIController(ltiDao);
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
