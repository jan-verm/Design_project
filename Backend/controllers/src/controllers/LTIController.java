package controllers;

import interfaces.LTIControllerInterface;

import java.math.BigInteger;
import java.security.SecureRandom;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.interfaces.ILtiDao;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author ?
 */
public class LTIController implements LTIControllerInterface {

    private final ILtiDao ltiDao;
    private final SecureRandom random;
    private static final String utf8 = "UTF-8";
    /**
     * create a lti controller
     *
     * @param ltiDao class that interacts with the db for lti
     */
    public LTIController(ILtiDao ltiDao) {
        this.ltiDao = ltiDao;
        this.random = new SecureRandom();
    }

    @Override
    public String generateKeySecretPair(String key) throws ClassicDatabaseException {
        String secret = new BigInteger(130, random).toString(32);
        ltiDao.addKeySecretPair(key, secret);
        return secret;
    }
    
    @Override
    public void deleteKeySecretPair(String key) throws ClassicDatabaseException, ClassicNotFoundException {
        ltiDao.deleteKeySecretPair(key);
    }
    
    @Override
    public String getSecret(String key) throws ClassicDatabaseException, ClassicNotFoundException {
        return ltiDao.getSecret(key);
    }
    
    @Override
    public long getTimeDifference(long parseLong) {
        long currentTime = System.currentTimeMillis()/1000;
        return Math.abs(currentTime - parseLong);
    }
    
    @Override
    public String generateOAuthSignature(String secret, Map<String,String> formData, String httpMethod, String baseURL) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException{
        Map<String,String> rawdataMap = new HashMap<>();
        // OAuth encode the customer secret and add an &.
        String encodedSecret = URLEncoder.encode(secret, utf8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~") + "&";

        // OAuth encode all key and value pairs.
        Iterator it = formData.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            // The recieved OAuth signature can not be in the signature base string.
            if ("oauth_signature".equals(pair.getKey().toString())){ continue;}
            // OAuth encodes some characters differently than URLEncoder thus replace.
            String key = URLEncoder.encode(pair.getKey().toString(), utf8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
            String value = URLEncoder.encode(((LinkedList)pair.getValue()).getFirst().toString(), utf8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
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
            if (first) { first = false; }
            else { output += "&"; }
            output += key + "=" + rawdataMap.get(key);
        }

        // Form the signature base string, OAuth encode the base url AND re-encode the parameter string. 
        String signatureBase = httpMethod + "&" +
                URLEncoder.encode(baseURL, utf8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~") + "&" +
                URLEncoder.encode(output, utf8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");

        // Initialize the HMAC-SHA1 encoder.
        SecretKeySpec keySpec = new SecretKeySpec(encodedSecret.getBytes(),"HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(keySpec);

        // Calculate the signature from the signature base string.
        byte[] rawHmac = mac.doFinal(signatureBase.getBytes());
        // Convert the byte signature to base 64. 
        return DatatypeConverter.printBase64Binary(rawHmac);    
    }
}
