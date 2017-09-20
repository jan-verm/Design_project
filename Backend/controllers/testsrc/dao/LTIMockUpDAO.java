/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.interfaces.ILtiDao;

/**
 *
 * @author DRIEK
 */
public class LTIMockUpDAO implements ILtiDao{

    @Override
    public void addKeySecretPair(String key, String secret) throws ClassicDatabaseException {
        if(key.equals("testclassic")){
            throw new ClassicDatabaseException();
        }
        
    }

    @Override
    public void deleteKeySecretPair(String key) throws ClassicDatabaseException, ClassicNotFoundException {
        switch (key) {
            case "testclassic":
                throw new ClassicNotFoundException();
            case "testclassic2":
                throw new ClassicDatabaseException();
        }
    }

    @Override
    public String getSecret(String key) throws ClassicDatabaseException, ClassicNotFoundException {
        switch (key) {
            case "testclassic":
                throw new ClassicNotFoundException();
            case "testclassic2":
                throw new ClassicDatabaseException();
            default:
                return "testclassic";
        }
    }

    @Override
    public void cleanTable() throws ClassicDatabaseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
