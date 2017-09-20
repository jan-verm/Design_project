package controllers;

import interfaces.UserControllerInterface;

import java.util.List;

import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import databaseaccess.interfaces.IUserDao;
import exceptions.ClassicCharacterNotAllowedException;

/**
 * @author Juta
 */
public class UserController implements UserControllerInterface{
    
    private final IUserDao dao;
    
    /**
     * create a user controller
     * @param dao class that interacts with the db for users
     */
    public UserController(IUserDao dao){
        this.dao = dao;
    }
    
    private void checkOwnerShip(int userId, int authId) throws ClassicDatabaseException, ClassicNotFoundException {
        User user = dao.getUser(authId);
        if (userId != authId && user.getRole() != Role.ADMIN) {
            throw new ClassicUnauthorizedException("user with id " + authId + " is not allowed to perform this action");
        }
    }

    @Override
    public int addUser(User user) throws ClassicDatabaseException {
        if(user.getUsername().indexOf('#')>=0){
            throw new ClassicCharacterNotAllowedException("# is not an allowed character in a username.");
        } else {
            return dao.addUser(user);
        }
    }
    
    @Override
    public int addLTIUser(User user) throws ClassicDatabaseException {
        return dao.addUser(user);
    }
    
    @Override
    public User getUser(String username) throws ClassicDatabaseException, ClassicNotFoundException {
        return dao.getUser(username);
    }

    @Override
    public User getUser(int userid) throws ClassicDatabaseException, ClassicNotFoundException {
         User u = dao.getUser(userid);
        u.setSubscriptions(dao.getSubscriptions(userid));
        return u;
    }

    @Override
    public void subscribeCourse(int userId, int courseId) throws ClassicDatabaseException, ClassicNotFoundException {
        dao.subscribeCourse(userId, courseId);
    }

    @Override
    public void unSubscribeCourse(int userId, int courseId) throws ClassicDatabaseException, ClassicNotFoundException {
        dao.unsubscribeCourse(userId, courseId);
    }

    @Override
    public List<User> getUsers() throws ClassicDatabaseException, ClassicNotFoundException {
        List<User> users = dao.getUsers();
        for(User u: users) {
            u.setSubscriptions(dao.getSubscriptions(u.getId()));
        }
        return users;
    }

    @Override
    public void updateUser(int updateUserid, User user, int authenticatedUserId) throws ClassicDatabaseException, ClassicNotFoundException {
        checkOwnerShip(updateUserid, authenticatedUserId);
        user.setId(updateUserid);
        dao.updateUser(user);
    }

    @Override
    public void deleteUser(int deleteUserid, int authenticatedUserId) throws ClassicDatabaseException, ClassicNotFoundException {
        checkOwnerShip(deleteUserid, authenticatedUserId);
        dao.deleteUser(deleteUserid);
    }
    
}
