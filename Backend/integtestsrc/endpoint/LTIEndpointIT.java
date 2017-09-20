package endpoint;

import config.ITConfig;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ITConfig.class})
@IntegrationTest
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test.properties")
public class LTIEndpointIT {

    private MockMvc mock;
    private Map<String, List<String>> form;
    private String secret;
    private String HTTPMethod;
    private String url;
    private static final String utf8 = "UTF-8";
    private int adminUserId;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        mock = webAppContextSetup(webApplicationContext).build();

        form = new HashMap<>();
        List<String> oauth_version = new LinkedList<>(Arrays.asList("1.0"));
        List<String> lti_message_type = new LinkedList<>(Arrays.asList("basic-lti-launch-request"));
        List<String> lti_version = new LinkedList<>(Arrays.asList("LTI-1p0"));
        List<String> oauth_consumer_key = new LinkedList<>(Arrays.asList("testclassic"));
        List<String> resource_link_id = new LinkedList<>(Arrays.asList("1"));
        List<String> oauth_callback = new LinkedList<>(Arrays.asList("about:blank"));
        // List<String> oauth_timestamp = new LinkedList<>(Arrays.asList("1462546654")); must be put when timestamp is made
        List<String> oauth_nonce = new LinkedList<>(Arrays.asList("d24c99c8275b4a360084037e43cc29e9"));
        List<String> oauth_signature = new LinkedList<>(Arrays.asList("lDEd/Ae49WfoBXYGtuHBFl5g9GA="));
        List<String> ext_user_username = new LinkedList<>(Arrays.asList("admin"));
        List<String> roles = new LinkedList<>(Arrays.asList("Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"));
        form.put("oauth_version", oauth_version);
        form.put("lti_message_type", lti_message_type);
        form.put("lti_version", lti_version);
        form.put("oauth_consumer_key", oauth_consumer_key);
        form.put("resource_link_id", resource_link_id);
        form.put("oauth_callback", oauth_callback);
        form.put("oauth_nonce", oauth_nonce);
        form.put("oauth_signature", oauth_signature);
        form.put("ext_user_username", ext_user_username);
        form.put("roles", roles);
        HTTPMethod = "POST";
        url = "http://student-dp8.intec.ugent.be/development/api/lti";

        // create user
        String userRequestBody = "{\"username\": \"admin\", \"password\": \"admin\", \"role\": \"admin\"}";

        String response = 
                mock.perform(post("/user")
    		.content(userRequestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        //save adminUserId
        JSONObject json = new JSONObject(response);
        adminUserId = json.getInt("userId");

        // login
        String loginBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
        mock.perform(post("/login")
                .content(loginBody))
                .andExpect(status().isOk());
        
        String requestBody = "{\"key\": \"testclassic\"}";
    	response = mock.perform(post("/lti/keypair")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("testclassic"))
                .andReturn().getResponse().getContentAsString();
        
        //save secret
        json = new JSONObject(response);
        secret = json.getString("secret");

        mock.perform(post("/logout"))
                .andExpect(status().isOk());
    }

    @After
    public void breakDown() throws Exception {
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";

        mock.perform(post("/login")
                .content(requestBody))
                .andExpect(status().isOk());
        
        //delete key-secret pair
        String delRequestBody = "{\"key\": \"testclassic\"}";
    	mock.perform(delete("/lti/keypair")
                .content(delRequestBody))
    		.andExpect(status().isOk());
        
        // delete user
        mock.perform(delete("/user/" + adminUserId))
                .andExpect(status().isOk());

        mock.perform(post("/logout"))
                .andExpect(status().isOk());  
    }

    /**
     * Test of lti method, of class LTIEndpoint.
     */
    @Test
    public void testLtiSuccess() throws Exception {
        // Test for success.
        System.out.println("START - SUCCESS - TEST");
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        List<String> oauth_timestamp = new LinkedList<>(Arrays.asList(timestamp));
        form.put("oauth_timestamp", oauth_timestamp); //needs to be removed after this method
        String signature = generateOAuthSignature(secret, (Map) form, HTTPMethod, url);

        String response = mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "1.0")
                .param("lti_message_type", "basic-lti-launch-request")
                .param("lti_version", "LTI-1p0")
                .param("oauth_consumer_key", "testclassic")
                .param("resource_link_id", "1")
                .param("oauth_callback", "about:blank")
                .param("oauth_timestamp", timestamp)
                .param("oauth_nonce", "d24c99c8275b4a360084037e43cc29e9")
                .param("oauth_signature", signature)
                .param("ext_user_username", "admin")
                .param("roles", "Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"))
                .andExpect(status().is3xxRedirection())
                .andReturn().getResponse().getRedirectedUrl();
        
        form.remove("oauth_timestamp"); //remove the timestamp from the map
        System.out.println("response: " + response); //trying to uncover userId, run locally to see output
        
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
        mock.perform(post("/login")
                .content(requestBody))
                .andExpect(status().isOk());
        // delete newly created user
        String getuserresponse = mock.perform(get("/userByName/admin%23testclassic"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println("username response: " + getuserresponse);
        JSONObject json = new JSONObject(getuserresponse);
        mock.perform(delete("/user/" + json.getString("userId")))
                .andExpect(status().isOk());
        System.out.println("deleted");
        mock.perform(post("/logout"))
                .andExpect(status().isOk());  
    }

    @Test
    public void testLtiDuplicateNonce() throws Exception {
        System.out.println("START - NONCE - TEST");
        // Try success test.
        //testLtiSuccess();
        // Test for duplicate nonce.
        mock.perform(post("/lti")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("oauth_version", "1.0")
            .param("lti_message_type", "basic-lti-launch-request")
            .param("lti_version", "LTI-1p0")
            .param("oauth_consumer_key", "testclassic")
            .param("resource_link_id", "1")
            .param("oauth_callback", "about:blank")
            .param("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000))
            .param("oauth_nonce", "d24c99c8275b4a360084037e43cc29e9")
            .param("oauth_signature", "lDEd/Ae49WfoBXYGtuHBFl5g9GA=")
            .param("ext_user_username", "admin")
            .param("roles", "Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"))
            .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testLtiUnsucReqValidation() throws Exception {
        System.out.println("START - UNSUCVAL - TEST");
        // Test for unsuccessfull request validation.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is4xxClientError());
        
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "2.0") // different
                .param("lti_message_type", "wrong") // different
    		.param("lti_version", "wrong") // different
                .param("oauth_consumer_key", "") // different
                .param("resource_link_id", "") // different
                .param("oauth_callback", "wrong") // different
                .param("oauth_timestamp", "") // different
                .param("oauth_nonce", "") // different
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuHBFl5g9GA=")
                .param("ext_user_username", "admin")
                .param("roles", "Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testLtiTooOld() throws Exception {
        System.out.println("START - TOOOLD - TEST");
        // Test for too old timestamp.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "1.0")
                .param("lti_message_type", "basic-lti-launch-request")
    		.param("lti_version", "LTI-1p0")
                .param("oauth_consumer_key", "testclassic")
                .param("resource_link_id", "1")
                .param("oauth_callback", "about:blank")
                .param("oauth_timestamp", "1562556654") // different
                .param("oauth_nonce", "d24c99c8275b4a360084037e43cc29e9")
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuHBFl5g9GA=")
                .param("ext_user_username", "admin")
                .param("roles", "Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testLtiKeyNotFound() throws Exception {
        System.out.println("START - KEYNF - TEST");
        // Test for key not found.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "1.0")
                .param("lti_message_type", "basic-lti-launch-request")
    		.param("lti_version", "LTI-1p0")
                .param("oauth_consumer_key", "notfound") // different
                .param("resource_link_id", "1")
                .param("oauth_callback", "about:blank")
                .param("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000))
                .param("oauth_nonce", "d24c99c8375b4a360084037e43cc29e9") // different
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuHBFl5g9GA=")
                .param("ext_user_username", "admin")
                .param("roles", "Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testLtiSecretMismatch() throws Exception {
        System.out.println("START - SECRETMISM - TEST");
        // Testcase when secrets do not match.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "1.0")
                .param("lti_message_type", "basic-lti-launch-request")
    		.param("lti_version", "LTI-1p0")
                .param("oauth_consumer_key", "testclassic")
                .param("resource_link_id", "1")
                .param("oauth_callback", "about:blank")
                .param("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000))
                .param("oauth_nonce", "d24c99c8375b4a360004037e43cc29e9") // different
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuABFl5g9GA=") // different
                .param("ext_user_username", "admin")
                .param("roles", "Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"))
                .andExpect(status().is4xxClientError());
    }
    
//    @Test
//    public void testLtiUserNotPresentYet() throws Exception {
//        System.out.println("START - USERNOTPRES - TEST");
//        form.remove("ext_user_username");
//        String username = "Driek";
//        List<String> ext_user_username = new LinkedList<>(Arrays.asList(username));
//        form.put("ext_user_username", ext_user_username);
//        
//        form.remove("oauth_nonce");
//        String nonce = "124c99c8375b4a360004037e43cc29e9";
//        List<String> oauth_nonce = new LinkedList<>(Arrays.asList(nonce));
//        form.put("oauth_nonce", oauth_nonce);
//        
//        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
//        List<String> oauth_timestamp = new LinkedList<>(Arrays.asList(timestamp));
//        form.put("oauth_timestamp", oauth_timestamp); //needs to be removed after this method
//        String signature = generateOAuthSignature(secret, (Map) form, HTTPMethod, url);
//        
//        // Test when lti user is not yet present in the system.
//        String response = mock.perform(post("/lti")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .param("oauth_version", "1.0")
//                .param("lti_message_type", "basic-lti-launch-request")
//    		.param("lti_version", "LTI-1p0")
//                .param("oauth_consumer_key", "testclassic")
//                .param("resource_link_id", "1")
//                .param("oauth_callback", "about:blank")
//                .param("oauth_timestamp", timestamp) // different
//                .param("oauth_nonce", nonce) // different
//                .param("oauth_signature", signature) // different
//                .param("ext_user_username", username) // different
//                .param("roles", "Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"))
//                .andExpect(status().is3xxRedirection())
//                .andReturn().getResponse().getRedirectedUrl();
//        
//        form.remove("oauth_timestamp"); //remove the timestamp from the map
//        System.out.println("response: " + response);
//        
//        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
//        mock.perform(post("/login")
//                .content(requestBody))
//                .andExpect(status().isOk());
//        // delete newly created user
//        String getuserresponse = mock.perform(get("/userByName/"+username+"%23testclassic"))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//        System.out.println("username response: " + getuserresponse);
//        JSONObject json = new JSONObject(getuserresponse);
//        mock.perform(delete("/user/" + json.getString("userId")))
//                .andExpect(status().isOk());
//        System.out.println("deleted");
//        mock.perform(post("/logout"))
//                .andExpect(status().isOk());  
//    }
//    
    
    
    /**
     * Method for generating the OAuth signature. Plain copy of method in LTIController due to no access.
     * @param secret
     * @param formData
     * @param HTTPMethod
     * @param baseURL
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException 
     */
    public String generateOAuthSignature(String secret, Map<String, List<String>> formData, String HTTPMethod, String baseURL) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        Map<String, String> rawdataMap = new HashMap<>();
        // OAuth encode the customer secret and add an &.
        String encodedSecret = URLEncoder.encode(secret, utf8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~") + "&";

        // OAuth encode all key and value pairs.
        Iterator it = formData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            // The recieved OAuth signature can not be in the signature base string.
            if ("oauth_signature".equals(pair.getKey().toString())) {
                continue;
            }
            // OAuth encodes some characters differently than URLEncoder thus replace.
            String key = URLEncoder.encode(pair.getKey().toString(), utf8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
            String value_encoded = URLEncoder.encode(((LinkedList) pair.getValue()).getFirst().toString(), utf8);
            String value = value_encoded.replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
            // Save encoded key and value pair to new map to retain old values.
            rawdataMap.put(key, value);
        }
        // Sort key list.
        List<String> sorted = new ArrayList<>(rawdataMap.keySet());
        java.util.Collections.sort(sorted);

        // Build the parameter string: key=value&key=value&key=&key=value and so on.
        String output = "";
        boolean first = true;
        for (String key : sorted) {
            if (first) {
                first = false;
            } else {
                output += "&";
            }
            output += key + "=" + rawdataMap.get(key);
        }

        // Form the signature base string, OAuth encode the base url AND re-encode the parameter string. 
        String signatureBase = HTTPMethod + "&"
                + URLEncoder.encode(baseURL, utf8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~") + "&"
                + URLEncoder.encode(output, utf8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");

        // Initialize the HMAC-SHA1 encoder.
        SecretKeySpec keySpec = new SecretKeySpec(encodedSecret.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(keySpec);

        // Calculate the signature from the signature base string.
        byte[] rawHmac = mac.doFinal(signatureBase.getBytes());
        // Convert the byte signature to base 64. 
        return DatatypeConverter.printBase64Binary(rawHmac);
    }
}
