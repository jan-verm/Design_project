package endpoint;

import interfaces.UserControllerInterface;
import interfaces.VideoControllerInterface;

import java.util.List;

import models.Video;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import parser.VideoParser;
import security.AuthenticationChecker;
import config.AbstractConfig;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import exceptionhandler.ExceptionHandler;
import exceptionhandler.Runner;

/**
 * @author Jan Vermeulen
 */
@RestController
public class VideoEndpoint {

    private VideoControllerInterface videoController;
    private UserControllerInterface userController;
    private ExceptionHandler exceptionHandler;
    private AuthenticationChecker authChecker;

    /**
     * Create a new VideoEndpoint and initialize controller dependencies.
     *
     * @return VideoEndpoint
     */
    @Autowired
    public VideoEndpoint(AbstractConfig config) {
        videoController = config.getVideoController();
        userController = config.getUserController();
        exceptionHandler = config.getExceptionHandler();
        authChecker = config.getAuthenticationChecker();
    }

    /**
     * Add a new video
     *
     * @param Id of the course
     * @param Id of the lecture
     * @return New video object
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/video", method = RequestMethod.POST)
    public ResponseEntity<String> postVideo(@PathVariable final int courseId, @PathVariable final int lectureId, @RequestBody final String body) {

        return new Runner(exceptionHandler, authChecker, userController, "postVideo") {
            @Override
            public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                String response = "";

                Video video = VideoParser.to(body);
                int id = videoController.addVideo(getParentId(courseId, lectureId), video, authChecker.getCurrentUserId());
                response = VideoParser.from(videoController.getVideo(id)).toString();

                return new ResponseEntity<String>(response, HttpStatus.OK);
            }
        }.runAndHandle();

    }

    /**
     * Delete a video
     *
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the video
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/video/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteVideo(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int id) {

        return new Runner(exceptionHandler, authChecker, userController, "deleteVideo") {
            @Override
            public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                videoController.deleteVideo(id, authChecker.getCurrentUserId());
                return new ResponseEntity<String>(HttpStatus.OK);
            }
        }.runAndHandle();

    }

    /**
     * Update a video
     *
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the video
     * @param JSON string containing the updated video
     * @return Updated video object
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/video/{videoId}", method = RequestMethod.PATCH)
    public ResponseEntity<String> updateVideo(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int videoId, @RequestBody final String body) {

        return new Runner(exceptionHandler, authChecker, userController, "updateVideo") {
            @Override
            public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                Video video = null;

                video = VideoParser.to(body);
                videoController.updateVideo(videoId, video, authChecker.getCurrentUserId());

                return new ResponseEntity<String>(HttpStatus.OK);
            }
        }.runAndHandle();

    }

    /**
     * Retrieve a certain a video
     *
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the video
     * @return Video object
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/video/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> getVideo(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int id) {

        return new Runner(exceptionHandler, authChecker, userController, "getVideo") {
            @Override
            public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                Video video = null;
                String videoJSON = "";

                video = videoController.getVideo(id);

                videoJSON = VideoParser.from(video).toString();
                System.out.println("Answered: " + videoJSON);

                return new ResponseEntity<String>(videoJSON, HttpStatus.OK);
            }
        }.runAndHandle();

    }

    /**
     * Retrieve all videos from a certain lecture or course.
     * If lectureId = 0, the video is course-specific, not lecture-specific.
     *
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the video
     * @return Video object
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/video", method = RequestMethod.GET)
    public ResponseEntity<String> getVideos(@PathVariable final int courseId, @PathVariable final int lectureId) {

        return new Runner(exceptionHandler, authChecker, userController, "getVideos") {
            @Override
            public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                List<Video> videoList = null;
                String videoListJSON = "";

                videoList = videoController.getAllVideos(getParentId(courseId, lectureId));

                videoListJSON = VideoParser.fromList(videoList).toString();
                System.out.println("Answered: " + videoListJSON);

                return new ResponseEntity<String>(videoListJSON, HttpStatus.OK);
            }
        }.runAndHandle();

    }

    private int getParentId(int courseId, int lectureId) {
        int parentId = courseId;
        if (lectureId != 0) {
            parentId = lectureId;
        }
        return parentId;
    }

}
