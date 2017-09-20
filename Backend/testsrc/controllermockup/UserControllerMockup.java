/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllermockup;

import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import exceptions.ClassicCharacterNotAllowedException;
import interfaces.UserControllerInterface;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Juta
 */
public class UserControllerMockup implements UserControllerInterface {

    @Override
    public int addUser(User user) throws ClassicDatabaseException {
        if (user.getUsername().contains("#")){
            throw new ClassicCharacterNotAllowedException();
        }
        return 1;
    }
    
    User savedUser = new User("", Role.NONE, null);
    @Override
    public int addLTIUser(User user) throws ClassicDatabaseException {
        savedUser = user;
        return 2;
    }

    @Override
    public User getUser(String username) throws ClassicDatabaseException, ClassicNotFoundException {
        User user = null;
        switch (username) {
            case "admin#testclassic":
                user = new User("admin#testclassic", Role.TEACHER, "$2a$10$qQ7kHNnc.o1kWLxf9DXzFeVa9bTOFPkzErzeQjLPN.kQcxRv7nSoG");
                user.setId(1);
                break;
            case "Driek#testclassic":
                if (savedUser.getUsername().equals("Driek#testclassic")){
                    return savedUser;
                }
                throw new ClassicNotFoundException();
            case "student#testclassic":
                if (savedUser.getUsername().equals("student#testclassic")){
                    return savedUser;
                }
                throw new ClassicNotFoundException();
            case "assistant#testclassic":
                if (savedUser.getUsername().equals("assistant#testclassic")){
                    return savedUser;
                }
                throw new ClassicNotFoundException();
            case "mentor#testclassic":
                if (savedUser.getUsername().equals("mentor#testclassic")){
                    return savedUser;
                }
                throw new ClassicNotFoundException();
            case "instructor#testclassic":
                if (savedUser.getUsername().equals("instructor#testclassic")){
                    return savedUser;
                }
                throw new ClassicNotFoundException();
            default:
                user = new User("admin", Role.ADMIN, "$2a$10$5qAAVhuNf1aD3tq5lMj.VOcDyVYN6bjkSMWQTwlgGt02aPli6CTW.");
                user.setId(1);
                break;
            
        }
        return user;
    }

    @Override
    public User getUser(int userid) throws ClassicDatabaseException, ClassicNotFoundException {
        return getUser("");
    }

    @Override
    public void subscribeCourse(int userId, int courseId) throws ClassicDatabaseException, ClassicNotFoundException {
        
    }

    @Override
    public void unSubscribeCourse(int userId, int courseId) throws ClassicDatabaseException, ClassicNotFoundException {
        
    }

    @Override
    public List<User> getUsers() throws ClassicDatabaseException {
        List<User> users = new ArrayList<>();
        User user = new User("admin", Role.ADMIN, "$2a$10$5qAAVhuNf1aD3tq5lMj.VOcDyVYN6bjkSMWQTwlgGt02aPli6CTW.");
        user.setId(1);
        users.add(user);
        return users;
        
    }

    @Override
    public void updateUser(int userid, User user, int authId) throws ClassicDatabaseException, ClassicNotFoundException {
        
    }

    @Override
    public void deleteUser(int userid, int authId) throws ClassicDatabaseException, ClassicNotFoundException {
        
    }
    
}
