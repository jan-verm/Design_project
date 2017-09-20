package endpoint;

import config.ITConfig;
import java.io.File;
import java.nio.file.Files;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ITConfig.class})
@IntegrationTest
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test.properties")
public class CourseNotesEndpointIT {

    private MockMvc mock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private int courseId;
    private int userId;

    @Before
    public void setup() throws Exception {
        mock = webAppContextSetup(webApplicationContext).build();

        // create user
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\", \"role\": \"admin\"}";

        String response
                = mock.perform(post("/user")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        userId = json.getInt("userId");

        // login
        requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";

        mock.perform(post("/login")
                .content(requestBody))
                .andExpect(status().isOk());

        // create course
        requestBody = "{\"name\": \"course1\"}";

        response
                = mock.perform(post("/course")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        json = new JSONObject(response);
        courseId = json.getInt("courseId");
    }

    @After
    public void tearDown() throws Exception {
        //delete course
        mock.perform(delete("/course/" + courseId))
                .andExpect(status().isOk());

        // delete user
        mock.perform(delete("/user/" + userId))
                .andExpect(status().isOk());

        // logout
        mock.perform(post("/logout"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCourseNotes() throws Exception {
        String requestBody = "{\"name\": \"title\", \"url\": \"url\"}";

        String response = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes")
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("courseNotesId");

        mock.perform(get("/course/" + courseId + "/lecture/0/coursenotes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseNotesId").value(id))
                .andExpect(jsonPath("$.name").value("title"))
                .andExpect(jsonPath("$.url").value("url"));

        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCourseNotesNotFoundException() throws Exception {
        mock.perform(get("/course/" + courseId + "/lecture/0/coursenotes/0"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetAllCourseNotes() throws Exception {
        String requestBody = "{\"name\": \"title\", \"url\": \"url\"}";

        String response = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes")
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("courseNotesId");

        mock.perform(get("/course/" + courseId + "/lecture/0/coursenotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseNotesId").value(id))
                .andExpect(jsonPath("$[0].name").value("title"))
                .andExpect(jsonPath("$[0].url").value("url"));

        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllCourseNotesIsEmpty() throws Exception {
        String response = mock.perform(get("/course/" + courseId + "/lecture/0/coursenotes"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONArray array = new JSONArray(response);
        assertEquals(0, array.length());
    }

    @Test
    public void testPostCourseNotes() throws Exception {
        String requestBody = "{\"name\": \"title\", \"url\": \"url\"}";

        String response = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes")
                .content(requestBody))
                .andExpect(jsonPath("$.courseNotesId").exists())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("courseNotesId");

        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testPostCourseNotesBadRequest() throws Exception {
        String requestBody = "{\"name\": \"title\" \"url\": \"url\"}";

        mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes")
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPatchCourseNotes() throws Exception {
        String requestBody = "{\"name\": \"title\", \"url\": \"url\"}";

        String response = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes")
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("courseNotesId");

        String updaterequestBody = "{\"name\": \"newtitle\", \"url\": \"newurl\"}";
        mock.perform(patch("/course/" + courseId + "/lecture/0/coursenotes/" + id)
                .content(updaterequestBody))
                .andExpect(status().isOk());

        mock.perform(get("/course/" + courseId + "/lecture/0/coursenotes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseNotesId").value(id))
                .andExpect(jsonPath("$.name").value("newtitle"))
                .andExpect(jsonPath("$.url").value("newurl"));

        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testPatchCourseNotesBadRequest() throws Exception {
        String requestBody = "{\"name\": \"title\", \"url\": \"url\"}";

        String response = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes")
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("courseNotesId");

        String updaterequestBody = "{\"name\": \"newtitle\" \"url\": \"newurl\"}";
        mock.perform(patch("/course/" + courseId + "/lecture/0/coursenotes/" + id)
                .content(updaterequestBody))
                .andExpect(status().isBadRequest());

        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testPatchCourseNotesNotFoundException() throws Exception {
        String updaterequestBody = "{\"name\": \"newtitle\", \"url\": \"newurl\"}";
        mock.perform(patch("/course/" + courseId + "/lecture/0/coursenotes/0")
                .content(updaterequestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testDeleteCourseNotesNotFoundException() throws Exception {
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/0"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testHandleFileUpload() throws Exception {
        System.out.println("test");
        MockMultipartFile file = new MockMultipartFile("file", "name", "text/plain", "mock pdf text".getBytes());

        String response = mock.perform(MockMvcRequestBuilders.fileUpload("/course/" + courseId + "/lecture/0/upload")
                .file(file)
                .param("name", "name.pdf"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("courseNotesId");
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
                .andExpect(status().isOk());
    }
}
