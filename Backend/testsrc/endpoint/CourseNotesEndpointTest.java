package endpoint;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import config.TestConfig;
import java.io.File;
import java.nio.file.Files;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestConfig.class})
@IntegrationTest
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test.properties")
public class CourseNotesEndpointTest {

    private MockMvc mock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        mock = webAppContextSetup(webApplicationContext).build();

        // login
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";

        mock.perform(post("/login")
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCourseNotes() throws Exception {
        mock.perform(get("/course/1/lecture/0/coursenotes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseNotesId").value(123))
                .andExpect(jsonPath("$.name").value("title"))
                .andExpect(jsonPath("$.url").value("url"));
    }

    @Test
    public void testGetCourseNotesNotFoundException() throws Exception {
        mock.perform(get("/course/1/lecture/0/coursenotes/0"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetAllCourseNotes() throws Exception {
        mock.perform(get("/course/1/lecture/0/coursenotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseNotesId").value(123))
                .andExpect(jsonPath("$[0].name").value("title"))
                .andExpect(jsonPath("$[0].url").value("url"));
    }

    @Test
    public void testPostCourseNotes() throws Exception {
        String requestBody = "{ \"courseNotesId\": 123, \"name\": \"title\", \"url\": \"url\"}";

        mock.perform(post("/course/1/lecture/0/coursenotes")
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void testPostLectureCourseNotes() throws Exception {
        String requestBody = "{ \"courseNotesId\": 123, \"name\": \"title\", \"url\": \"url\"}";

        mock.perform(post("/course/1/lecture/1/coursenotes")
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void testPostCourseNotesBadRequest() throws Exception {
        String requestBody = "{ \"courseNotesId\": 123 \"name\": \"title\", \"url\": \"url\"}";

        mock.perform(post("/course/1/lecture/0/coursenotes")
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPatchCourseNotes() throws Exception {
        String requestBody = "{ \"courseNotesId\": 123, \"name\": \"title\", \"url\": \"url2\"}";
        mock.perform(patch("/course/1/lecture/0/coursenotes/1")
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void testPatchCourseNotesNotFoundException() throws Exception {
        String requestBody = "{ \"courseNotesId\": 123, \"name\": \"title\", \"url\": \"url2\"}";
        mock.perform(patch("/course/1/lecture/0/coursenotes/0")
                .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testPatchCourseNotesBadRequest() throws Exception {
        String requestBody = "{ \"courseNotesId\": 123 \"name\": \"title\", \"url\": \"url2\"}";

        mock.perform(patch("/course/1/lecture/0/coursenotes/1")
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteCourseNotes() throws Exception {
        mock.perform(delete("/course/1/lecture/0/coursenotes/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteCourseNotesNotFoundException() throws Exception {
        mock.perform(delete("/course/1/lecture/0/coursenotes/0"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testHandleFileUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "name", "text/plain", "mock pdf text".getBytes());
        
        mock.perform(MockMvcRequestBuilders.fileUpload("/course/1/lecture/0/upload")
                .file(file)
                .param("name", "name.pdf"))
                .andExpect(status().isOk());
                

        Files.deleteIfExists(new File("/var/www/classic/resources/courses/name.pdf").toPath());
    }

}
