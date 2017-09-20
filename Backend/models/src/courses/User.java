/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courses;

import java.util.ArrayList;
import java.util.List;

/**
 * User object
 * @author Juta
 */
public class User {
    private int id;
    private String username;
    private String password;
    private List<Course> subscriptions;
    private Role role;
    
    /**
     * Create a user
     * @param username name of the user
     * @param role
     */
    public User(String username, Role role, String password) {
        this.username = username;
        this.role = role;
        this.password = password;
        subscriptions = new ArrayList<>();
    }

    /**
     * Returns the username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Changes the username
     * @param username new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the courses this user is subscribed to
     * @return list of courses
     */
    public List<Course> getSubscriptions() {
        return subscriptions;
    }
    
    /**
     * Adds a new subscription
     * @param course a course the user wants to subscribe to
     */
    public void addSubscription(Course course) {
        subscriptions.add(course);
    }

    /**
     * Changes the subscriptions for the user
     * @param subscriptions list of new subscriptions
     */
    public void setSubscriptions(List<Course> subscriptions) {
        this.subscriptions = subscriptions;
    }

    /**
     * Gets the role of the user
     * @return role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role of a user
     * @param role
     */
    public void setRole(Role role) {
        this.role = role;
    }
    
    /**
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
}
