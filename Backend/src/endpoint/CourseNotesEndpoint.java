package endpoint;

import interfaces.CourseNotesControllerInterface;
import interfaces.UserControllerInterface;

import java.util.List;

import models.CourseNotes;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import parser.CourseNotesParser;
import security.AuthenticationChecker;
import config.AbstractConfig;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import exceptionhandler.ExceptionHandler;
import exceptionhandler.Runner;
import interfaces.CommentControllerInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;


/**
 * @author Jan Vermeulen
 */
@RestController
public class CourseNotesEndpoint {

    private CourseNotesControllerInterface courseNotesController;
    private CommentControllerInterface commentController;
    private ExceptionHandler exceptionHandler;
    private AuthenticationChecker authChecker;
    private UserControllerInterface userController;

    /**
     * Create a new CourseNotesEndpoint and initialize controller dependencies.
     *
     * @return CourseNotesEndpoint
     */
    @Autowired
    public CourseNotesEndpoint(AbstractConfig config) {
        courseNotesController = config.getCourseNotesController();
        commentController = config.getCommentController();
        exceptionHandler = config.getExceptionHandler();
        authChecker = config.getAuthenticationChecker();
        userController = config.getUserController();
    }

    /**
     * Retrieve certain coursenotes.
     * 
     * @param Id of the course containing the coursenotes
     * @param Id of the lecture containing the coursenotes
     * @param Id of the coursenotes
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/coursenotes/{courseNotesId}", method = RequestMethod.GET)
    public ResponseEntity<String> getCourseNotes(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int courseNotesId) {


        return new Runner(exceptionHandler, authChecker, userController, "getCourseNotes") {
            @Override
            public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                String response = null;

                CourseNotes cn = courseNotesController.getCourseNotes(courseNotesId);
                response = CourseNotesParser.from(cn).toString();

                return new ResponseEntity<String>(response, HttpStatus.OK);
            }
        }.runAndHandle();

    }

    /**
     * Retrieve all coursenotes from a certain course and lecture.
     * If lectureId = 0, the coursenotes are course specific, not lecture specific.
     * 
     * @param Id of the course containing the coursenotes
     * @param Id of the lecture containing the coursenotes
     * @param Id of the coursenotes
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/coursenotes", method = RequestMethod.GET)
    public ResponseEntity<String> getAllCourseNotes(@PathVariable final int courseId, @PathVariable final int lectureId) {
        return new Runner(exceptionHandler, authChecker, userController, "getAllCourseNotes") {
            @Override
            public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                String response = "";

                List<CourseNotes> cn = courseNotesController.getAllCourseNotes(getParentId(courseId, lectureId));

                response = CourseNotesParser.fromList(cn).toString();

                return new ResponseEntity<String>(response, HttpStatus.OK);
            }
        }.runAndHandle();

    }

    /**
     * Add new coursenotes to a certain lecture or course.
     * If lectureId = 0, the coursenotes are course specific, not lecture specific.
     * 
     * @param Id of the course containing the coursenotes
     * @param Id of the lecture containing the coursenotes
     * @return New coursenotes object
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/coursenotes", method = RequestMethod.POST)
    public ResponseEntity<String> postCourseNotes(@PathVariable final int courseId, @PathVariable final int lectureId, @RequestBody final String body) {

        return new Runner(exceptionHandler, authChecker, userController, "postCourseNotes") {
            @Override
            public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                String response = "";

                CourseNotes cn = CourseNotesParser.to(body);
                int id = courseNotesController.addCourseNotes(getParentId(courseId, lectureId), cn, authChecker.getCurrentUserId());
                response = CourseNotesParser.from(courseNotesController.getCourseNotes(id)).toString();
                System.out.println(response);

                return new ResponseEntity<String>(response, HttpStatus.OK);
            }
        }.runAndHandle();

    }

    /**
     * Update coursenotes from a certain lecture or course.
     * If lectureId = 0, the coursenotes are course specific, not lecture specific.
     * 
     * @param Id of the course containing the coursenotes
     * @param Id of the lecture containing the coursenotes
     * @param Id of the coursenotes
     * @param Json string containing the new coursenotes object
     * @return Updated coursenotes object
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/coursenotes/{courseNotesId}", method = RequestMethod.PATCH)
    public ResponseEntity<String> patchCourseNotes(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int courseNotesId, @RequestBody final String body) {

        return new Runner(exceptionHandler, authChecker, userController, "patchCourseNotes") {
            @Override
            public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                String response = "";

                CourseNotes cn = CourseNotesParser.to(body);
                courseNotesController.updateCourseNotes(courseNotesId, cn, authChecker.getCurrentUserId());
                response = CourseNotesParser.from(courseNotesController.getCourseNotes(courseNotesId)).toString();

                return new ResponseEntity<String>(response, HttpStatus.OK);
            }
        }.runAndHandle();

    }

    /**
     * Delete a coursenotes object.
     * 
     * @param Id of the course containing the coursenotes
     * @param Id of the lecture containing the coursenotes
     * @param Id of the coursenotes
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/coursenotes/{courseNotesId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteCourseNotes(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int courseNotesId) {

        return new Runner(exceptionHandler, authChecker, userController, "deleteCourseNotes") {
            @Override
            public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                courseNotesController.deleteCourseNotes(courseNotesId, authChecker.getCurrentUserId());

                return new ResponseEntity<String>(HttpStatus.OK);
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
    
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/coursenotes/{courseNotesId}/download-none", method = RequestMethod.GET)
    public ResponseEntity<String> downloadNoneCourseNotes(final HttpServletResponse response, final HttpServletRequest request, @PathVariable int courseId,
            @PathVariable int lectureId, @PathVariable final int courseNotesId) throws ClassicDatabaseException, ClassicNotFoundException {
        return downloadPDF(response, request, courseNotesId, false, false);
    }

    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/coursenotes/{courseNotesId}/download-all", method = RequestMethod.GET)
    public ResponseEntity<String> downloadAllCourseNotes(final HttpServletResponse response, final HttpServletRequest request, @PathVariable int courseId,
            @PathVariable int lectureId, @PathVariable final int courseNotesId) throws ClassicDatabaseException, ClassicNotFoundException {
        return downloadPDF(response, request, courseNotesId, true, true);
    }
    
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/coursenotes/{courseNotesId}/download-questions", method = RequestMethod.GET)
    public ResponseEntity<String> downloadQuestionsCourseNotes(final HttpServletResponse response, final HttpServletRequest request, @PathVariable int courseId,
            @PathVariable int lectureId, @PathVariable final int courseNotesId) throws ClassicDatabaseException, ClassicNotFoundException {
        return downloadPDF(response, request, courseNotesId, false, true);
    }
    
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/coursenotes/{courseNotesId}/download-annotations", method = RequestMethod.GET)
    public ResponseEntity<String> downloadAnnotationsCourseNotes(final HttpServletResponse response, final HttpServletRequest request, @PathVariable int courseId,
            @PathVariable int lectureId, @PathVariable final int courseNotesId) throws ClassicDatabaseException, ClassicNotFoundException {
        return downloadPDF(response, request, courseNotesId, true, false);
    }
    
    private ResponseEntity<String> downloadPDF(final HttpServletResponse response, final HttpServletRequest request, @PathVariable final int courseNotesId,
            final boolean annotations, final boolean questions) throws ClassicDatabaseException, ClassicNotFoundException{
        return new Runner(exceptionHandler, authChecker, userController, "downloadCourseNotes") {
            @Override
            public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                String outputFileName = courseNotesController.addAllPdfComments(courseNotesController.getCourseNotes(courseNotesId),
                        commentController.getCourseNotesComments(courseNotesId), annotations, questions);
                File file = new File(outputFileName);
                ServletContext context = request.getServletContext();
                try (InputStream fileInputStream = new FileInputStream(file);
                    OutputStream output = response.getOutputStream();) {
                    // Get MIME type of the file, set to binary type if MIME mapping not found.
                    String mimeType = context.getMimeType(outputFileName);
                    if (mimeType == null) {
                        mimeType = "application/octet-stream";
                    }
                    
                    // Set content attributes for the response.
                    response.setContentType(mimeType);
                    response.setContentLength((int) (file.length()));
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
                    
                    IOUtils.copyLarge(fileInputStream, output);
                    output.flush();
                    
                    // Delete the file that was created by calling addAllPdfComments. This file has now been sent to the user and is not functional anymore.
                    Files.delete(file.toPath());
                    return new ResponseEntity<>("You successfully downloaded: " + file.getName() ,HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>("You failed to download " + file.getName(), HttpStatus.BAD_REQUEST);
                }
            }
        }.runAndHandle();
    } 
}
