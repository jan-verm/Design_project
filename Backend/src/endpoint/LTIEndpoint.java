package endpoint;

import interfaces.LTIControllerInterface;
import interfaces.UserControllerInterface;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import parser.LTIParser;
import security.AuthenticationChecker;
import config.AbstractConfig;
import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import exceptionhandler.ExceptionHandler;
import exceptionhandler.Runner;
import org.springframework.security.crypto.bcrypt.BCrypt;


@RestController
public class LTIEndpoint {

    @Autowired
    @Qualifier("classicAuthenticationManager")
    AuthenticationManager authManager;
    

    private Map<String,Long> nonceHistory = new HashMap<>();
    private static final long MAXTIMEDIFFERENCE = 500;
    private ExceptionHandler exceptionHandler;
    private UserControllerInterface userController;
    private LTIControllerInterface ltiController;
    private AuthenticationChecker authChecker;
    private String homeUrl;

    /**
     * Create a new LTIEndpoint and initialize controller dependencies.
     *
     * @return LTIEndpoint
     */
    @Autowired
    public LTIEndpoint(AbstractConfig config) {
        exceptionHandler = config.getExceptionHandler();
        userController = config.getUserController();
        authChecker = config.getAuthenticationChecker();
        ltiController = config.getLTIController();
        homeUrl = config.getHomeUrl();
    }
    
    /**
     * Log in with Single Sign On defined by Learning Tools Interoperability.
     * @param formData the form must contain a valid LTI launch request
     * @return reroute the user to the Classic home screen.
     */
    @RequestMapping(value = "/lti", method = RequestMethod.POST)
    public ResponseEntity<String> lti(@RequestBody final MultiValueMap<String,String> formData) {

        return new Runner(exceptionHandler, null, null, "noAuth") {
            @Override
            public ResponseEntity<String> action() throws JSONException, ClassicDatabaseException{
                String httpMethod = "POST";
                String url = homeUrl + "/api/lti";
                
                // Begin LTI launch request validation.
                Boolean ok = "basic-lti-launch-request".equals(formData.getFirst("lti_message_type"));
                ok = ok && ("LTI-1p0".equals(formData.getFirst("lti_version")) || "LTI-2p0".equals(formData.getFirst("lti_version")));
                ok = ok && !formData.getFirst("oauth_consumer_key").isEmpty();
                ok = ok && !formData.getFirst("resource_link_id").isEmpty();
                ok = ok && "1.0".equals(formData.getFirst("oauth_version"));
                ok = ok && "about:blank".equals(formData.getFirst("oauth_callback"));
                ok = ok && formData.getFirst("oauth_timestamp")!=null;
                ok = ok && formData.getFirst("oauth_nonce")!=null;
                
                // Do not proceed unless valid LTI launch request.
                if(!ok){ return new ResponseEntity<>("Not a valid LTI launch request. Information is missing.", HttpStatus.UNAUTHORIZED);}

                // Check that the timestamp is within a specified interval either side of the current server time.
                long difference = ltiController.getTimeDifference(Long.parseLong(formData.getFirst("oauth_timestamp")));
                ok = ok && -MAXTIMEDIFFERENCE < difference && difference < MAXTIMEDIFFERENCE ;
                if(!ok){ return new ResponseEntity<>("Invalid timestamp, too old.", HttpStatus.UNAUTHORIZED);}
                
                // Check the nonce.
                ok = ok && isNonceUnique(formData.getFirst("oauth_nonce"));
                if(!ok){ return new ResponseEntity<>("Duplicate nonce is not allowed.", HttpStatus.UNAUTHORIZED);}

                // Save current nonce to history.
                nonceHistory.put(formData.getFirst("oauth_nonce"), Long.parseLong(formData.getFirst("oauth_timestamp")));

                // Get the consumer secret from request and get secret from database.
                String consumerKey = formData.getFirst("oauth_consumer_key");
                String secret;
                try {
                    secret = ltiController.getSecret(consumerKey);
                } catch (ClassicNotFoundException ex) {
                    return new ResponseEntity<>("Invallid consumer key." + ex, HttpStatus.UNAUTHORIZED);
                }
                
                String result;
                try {
                    result = ltiController.generateOAuthSignature(secret, (Map)formData, httpMethod, url);
                } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException ex) {
                    return new ResponseEntity<>("An error occurred while calculating the OAuth signature." + ex,HttpStatus.UNAUTHORIZED);
                }
                
                // check if our encoded result matches the received encoded signature
                ok = ok && result.equals(formData.getFirst("oauth_signature"));
                // Do not proceed unless authenticated launch request.
                if(!ok){return new ResponseEntity<>("OAuth signatures do not match.", HttpStatus.UNAUTHORIZED);}
                
                // Construct the unique username of the LTI SSO requesting user.
                String ltiUsername = formData.getFirst("ext_user_username");
                String username = ltiUsername + "#" + consumerKey ;
                int userId = 0;
                // If the user does not already exist, create a new user with the unique username.
                try {
                    userId = userController.getUser(username).getId();
                } catch (ClassicNotFoundException ex) {
                    // Check roles and then assign one.
                    Role ltiRole = getUserRole(formData.getFirst("roles"));
                    userId = userController.addLTIUser(new User(username, ltiRole, BCrypt.hashpw("very_secret_lti_user_password", BCrypt.gensalt())));
                }
                
                // Authentication
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, "very_secret_lti_user_password");
                Authentication auth = authManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
                auth =  SecurityContextHolder.getContext().getAuthentication();
                GrantedAuthority authority = ((List<GrantedAuthority>) auth.getAuthorities()).get(0);
                String role = authority.getAuthority();
                
                try {
                    String ltiRedirectURL = homeUrl + "/#/lti?role=" + role 
                            + "&sessionId=" + sessionId 
                            + "&username=" + URLEncoder.encode(auth.getName(), "UTF-8") 
                            + "&id=" + userId;
                    return handleLTIResponse(ltiRedirectURL, HttpStatus.FOUND);
                } catch (UnsupportedEncodingException ex) {
                    return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
                }
            }
        }.runAndHandle();

    }
    
    /**
     * Generate a new LTI key-secret pair.
     * @param body Must contain the key for this new pair.
     * @return The key-secret pair in json format.
     */
    @RequestMapping(value = "/lti/keypair", method = RequestMethod.POST)
    public ResponseEntity<String> newLtiKeyPair(@RequestBody final String body) {
    	return new Runner(exceptionHandler, authChecker, userController, "newLtiKeyPair") {
                @Override
                public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                        String key = LTIParser.getKey(body);
                        String secret = ltiController.generateKeySecretPair(key);
                        String response = LTIParser.buildKeyPair(key, secret).toString();
                        return new ResponseEntity<>(response, HttpStatus.OK);
                }
        }.runAndHandle();
    }
    
    /**
     * Delete an existing LTI key-secret pair.
     * @param body Must contain the key for this pair.
     * @return An OK status for success.
     */
    @RequestMapping(value = "/lti/keypair", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteLtiKeyPair(@RequestBody final String body) {
    	return new Runner(exceptionHandler, authChecker, userController, "deleteLtiKeyPair") {
                @Override
                public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                    String key = LTIParser.getKey(body);
                    ltiController.deleteKeySecretPair(key);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
        }.runAndHandle();
    }
    
    private ResponseEntity<String> handleLTIResponse(String responseUrl, HttpStatus status){
        try {
            HttpHeaders headers = new HttpHeaders();
            URI uri = new URI(responseUrl);
            headers.setLocation(uri);
            return new ResponseEntity<>(null,headers,status);
        } catch (URISyntaxException ex) {
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Extract the user role from the request and map to Classic system users.
     * @param roles The roles content from request as string.
     * @return The role for the user.
     */
    private Role getUserRole(String roles){
        // No ADMIN roles are given out to lti users.
        // ADMIN role must be added manually to the database.
        if (roles.contains("Administrator")
                || roles.contains("Instructor")
                || roles.contains("Mentor")
                || roles.contains("TeachingAssistant")) {
            return Role.TEACHER;
        } else {
            return Role.STUDENT;
        }
    }

    /**
     * Check that the nonce does not have the same value as that of another message received, within a certain timeframe.
     * @param nonce
     * @return presence
     */
    private Boolean isNonceUnique(String nonce){
        long currentTime = System.currentTimeMillis()/1000;
        Iterator historyIt = nonceHistory.entrySet().iterator();
        while (historyIt.hasNext()) {
            Map.Entry pair = (Map.Entry)historyIt.next();
            if (pair.getKey().equals(nonce)){
                return false;
            }
            // Remove old nonces from history.
            if ((Long.parseLong(pair.getValue().toString())+MAXTIMEDIFFERENCE) < currentTime ){
                historyIt.remove();
            }
        }
        return true;
    }
}
