package com.example.letsdoit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The type User.
 */
public class User implements Serializable {

    // properties
    private String name;
    private String username;
    private String uid;
    private String password;
    private HashMap<String, User> friends;


    // constructors
    /**
     * Instantiates a new User.
     */
    public User(){
        // empty for database reference purposes
        name = "";
        username = "";
        uid = "";
        friends = new HashMap<String,User>();
    }

    /**
     * Instantiates a new User.     *
     * @param name     the name
     * @param username the username
     * @param uid      the uid
     * @param password the password
     */
    public User( String name, String username, String uid, String password){
        this.name = name;
        this.username = username;
        this.uid = uid;
        this.password = password;
        this.friends = new HashMap<String,User>();
    }

    // methods

    /**
     * Gets password.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets friends.
     * @return the friends
     */
    public HashMap<String, User> getFriends() {
        return friends;
    }

    /**
     * Sets friends.
     * @param friends the friends
     */
    public void setFriends(HashMap< String, User> friends) {
        this.friends = friends;
    }

    /**
     * Add friend boolean.
     * @param user the user
     * @return the boolean
     */
    public boolean addFriend( User user){
        if ( user == null)
            return false;
        friends.put( user.getUid() ,user);
        return true;
    }

    /**
     * Is a friend boolean.
     * @param user the user
     * @return the boolean
     */
    public boolean isAFriend( User user) {
        if( user == null)
            return false;

        if ( friends.containsKey( user.getUid())){
            return true;
        }
        return false;
    }

    /**
     * Gets name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets username.
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets uid.
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets uid.
     * @param uid the uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Equals boolean.
     * @param user the user
     * @return the boolean
     */
    public boolean equals( User user){

        if( uid.equals( user.getUid()))
            return true;
        return false;
    }
}

