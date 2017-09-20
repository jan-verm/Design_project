package endpoint;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import exceptionhandler.ExceptionHandler;
import exceptionhandler.Runner;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import config.AbstractConfig;
import interfaces.CourseNotesControllerInterface;
import interfaces.UserControllerInterface;
import java.io.IOException;
import java.net.URLEncoder;
import models.CourseNotes;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import parser.CourseNotesParser;
import security.AuthenticationChecker;

@RestController
public class FileUploadEndpoint {

    // Place new files in location.
    private final String location;
    private CourseNotesControllerInterface courseNotesController;
    private ExceptionHandler exceptionHandler;
    private AuthenticationChecker authChecker;
    private UserControllerInterface userController;

    /**
     * Create a new FileUploadEndpoint and initialize controller dependencies.
     */
    @Autowired
    public FileUploadEndpoint(AbstractConfig config) {
        courseNotesController = config.getCourseNotesController();
        exceptionHandler = config.getExceptionHandler();
        authChecker = config.getAuthenticationChecker();
        userController = config.getUserController();
        location = config.getPdfUploadLocation();
    }

    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/upload", method = RequestMethod.POST)
    public ResponseEntity<String> handleFileUpload(@PathVariable final int courseId, @PathVariable final int lectureId, @RequestParam("name") final String name, @RequestParam("file") final MultipartFile file) {

        return new Runner(exceptionHandler, authChecker, userController, "handleFileUpload") {
            @Override
            public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                if (name.contains("/")) {
                    return new ResponseEntity<String>("Folder separators not allowed", HttpStatus.BAD_REQUEST);
                }
                
                if (!file.isEmpty()) {
                    try {
                        String response = "";
                        String fileName = name.substring(0, name.lastIndexOf('.'));
                        // Encode filename to handle unwanted characters like space.
                        String pdfName =  URLEncoder.encode(fileName + System.currentTimeMillis() + ".pdf", "UTF-8");

                        File f = new File(location + "/" + pdfName);
                        f.createNewFile();
                        if (f.exists()) {
                            // writing to: 'getPdfUploadLocation()'/fileName.pdf
                            // file url in db: http://student-dp8.intec.ugent.be/resources/courses/fileName.pdf
                            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));
                            FileCopyUtils.copy(file.getInputStream(), stream);
                            stream.close();

                            // Emulate a parser and then send to controller
                            CourseNotes cn = new CourseNotes(fileName, "http://student-dp8.intec.ugent.be/resources/courses/" + pdfName);     
                            int id = courseNotesController.addCourseNotes(getParentId(courseId, lectureId), cn, authChecker.getCurrentUserId());
                            
                            response = CourseNotesParser.from(courseNotesController.getCourseNotes(id)).toString();
                            return new ResponseEntity<String>(response, HttpStatus.OK);
                        } else {
                            return new ResponseEntity<String>("The parent path is wrong.", HttpStatus.BAD_REQUEST);
                        }

                    } catch (IOException e) {
                        return new ResponseEntity<String>("You failed to upload " + name + " => " + e.getMessage(), HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<String>("You failed to upload " + name + " because the file was empty", HttpStatus.BAD_REQUEST);
                }
            }
        }.runAndHandle();

    }

    public int getParentId(int courseId, int lectureId) {
        int parentId = courseId;
        if (lectureId != 0) {
            parentId = lectureId;
        }
        return parentId;
    }
}
