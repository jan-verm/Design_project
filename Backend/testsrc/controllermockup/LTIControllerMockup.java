package controllermockup;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import interfaces.LTIControllerInterface;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class LTIControllerMockup implements LTIControllerInterface {

    @Override
    public String generateKeySecretPair(String key) throws ClassicDatabaseException {
        return "testclassic";
    }

    @Override
    public void deleteKeySecretPair(String key) throws ClassicDatabaseException, ClassicNotFoundException {
        
    }

    @Override
    public String getSecret(String key) throws ClassicDatabaseException, ClassicNotFoundException {
        if ( key.equals("testclassic")){
            return "testclassic";
        }
        throw new ClassicNotFoundException();
    }

    @Override
    public long getTimeDifference(long parseLong) {
        return 1462546654 - parseLong;
    }

    @Override
    public String generateOAuthSignature(String secret, Map<String, String> formData, String HTTPMethod, String url) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        if (secret.equals("usuccessfull")){
            throw new InvalidKeyException();
        }
        return "lDEd/Ae49WfoBXYGtuHBFl5g9GA=";
    }
}
