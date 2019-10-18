package com.example.letsdoit;


import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Comparator;

public class Entry implements Serializable {

    // properties
    private User user;
    private String text;
    private int ePoints;
    private int viewChoice;
    private String dateStr;
    private @ServerTimestamp Date timestamp;
    private String showName;
    private WallLocation loc;
    private String id;
    private HashMap<String, User> upvoters;
    private HashMap<String, User> downvoters;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public HashMap<String, User> getUpvoters() {
        return upvoters;
    }

    public void setUpvoters(HashMap<String, User> upvoters) {
        this.upvoters = upvoters;
    }

    public HashMap<String, User> getDownvoters() {
        return downvoters;
    }

    public void setDownvoters(HashMap<String, User> downvoters) {
        this.downvoters = downvoters;
    }

    public final static int CHAR_LIMIT = 256;

    public final static int USERNAME = 1;
    public final static int NAME = 0;
    public final static int ANON = -1;
    private final static String ID_UNIFIER = "*";

    public Entry(){
        // for database purposes
    }


    // methods


    public Entry(User user, String text, WallLocation loc, int viewChoice){
        this.text = ProfanityDetector.getFilteredText( text);
        this.user = user;
        this.viewChoice = viewChoice;
        this.loc = loc;

        if( viewChoice == USERNAME)
            showName = user.getUsername();
        else if( viewChoice == NAME)
            showName = user.getName();
        else if( viewChoice == ANON)
            showName = "anonymous";

        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        String s = formatter.format( new Date());
        dateStr = formatter.format(new Date());

        timestamp = new Date();

        ePoints = 0;

        upvoters = new HashMap< String, User>();
        downvoters = new HashMap< String, User>();

        id = user.getUid() + ID_UNIFIER + loc.getName() + ID_UNIFIER + randomNumber();

    }

    /**
     * Gets the current User Object
     * @return the User Object
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the current User object
     * @param user the User that is going to be set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the text written by the user
     * @return The text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text to the given text
     * @param text the Text that is going to be set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the ePoints
     * @return the ePoints as int
     */
    public int getePoints() {
        return ePoints;
    }

    /**
     * Sets the ePoints to the given value
     * @param ePoints the int value for ePoints
     */
    public void setePoints(int ePoints) {
        this.ePoints = ePoints;
    }

    /**
     * Returns the viewChoice of the user
     * @return theViewChoice as a int
     */
    public int getViewChoice() {
        return viewChoice;
    }

    /**
     * sets the View of the page
     * @param viewChoice the view choice as a int
     */
    public void setViewChoice(int viewChoice) {
        this.viewChoice = viewChoice;
    }

    /**
     * Gets the name of the user( username, anonymous etc.
     * @return the name of the user
     */
    public String getShowName() {
        return showName;
    }

    /**
     * Sets the name of the user( username, anonymous etc.)
     * @param showName the name that will be displayed below the entry
     */
    public void setShowName(String showName) {
        this.showName = showName;
    }

    /**
     * Gets the locations of wall
     * @return the location of the wall
     */
    public WallLocation getLoc() {
        return loc;
    }

    /**
     * Sets the wall location to a specific location
     * @param loc the location that the wall will be placed
     */
    public void setLoc(WallLocation loc) {
        this.loc = loc;
    }

    /**
     * gets the id of the user
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * sets the id of the user
     * @param id the id as a String
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * increments the ePoints by 1
     */
    public void upvote(){
        ePoints++;
    }

    /**
     * decrements the ePoints by 1
     */
    public void downvote(){
        ePoints--;
    }

    /**
     * Gets the date as a string
     * @return the date
     */
    public String getDateStr() {
        return dateStr;
    }

    /**
     * Sets the date to the given date String
     * @param dateStr the date as String
     */
    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    /**
     * Gets the date Object
     * @return the date Object
     */
    public Date getDate() {
        return timestamp;
    }

    /**
     * sets the Date object
     * @param date the date object that will be set
     */
    public void setDate(Date date) {
        this.timestamp = date;
    }

    /**
     * Creates and returns a random int
     * @return a random int
     */
    public int randomNumber(){
        int min = 1;
        int max = Integer.MAX_VALUE - 1;
        Random r = new Random();

        return r.nextInt((max - min) + 1) + min;
    }

    /**
     * Compares two entries and returns the difference of them
     */
    public static Comparator<Entry> EntryPointComparator = new Comparator<Entry>() {

        public int compare( Entry e1, Entry e2) {
            int e1Point = e1.getePoints();
            int e2Point = e2.getePoints();

            return e2Point - e1Point;
        }
    };

    /**
     * Compares tho entries according to their date
     */
    public static Comparator<Entry> EntryTimeComparator = new Comparator<Entry>() {
        @Override
        public int compare(Entry e1, Entry e2) {
            Date d1 = e1.getDate();
            Date d2 = e2.getDate();

            return d2.compareTo( d1);
        }
    };

    /**
     * Stores the user so that a user cannot upvote an entry unlimited times
     * @param user the user that upvotes an entry
     * @return true, if the user is added - false, otherwise
     */
    public boolean addUpvoter( User user){

        if( !upvoters.containsKey( user.getUid())){

            if( hasDownvoted( user)){

                downvoters.remove( user.getUid());
            }
            upvoters.put( user.getUid(), user);
            ePoints = upvoters.size() - downvoters.size();
            return true;
        }
        return false;
    }

    /**
     * Stores the user so that a user cannot downvote an entry unlimited times
     * @param user the user that upvotes an entry
     * @return true, if the user is added - false, otherwise
     */
    public boolean addDownvoter( User user){

        if( !downvoters.containsKey( user.getUid())){

            if ( hasUpvoted( user)){

                upvoters.remove( user.getUid());
            }
            downvoters.put( user.getUid(), user);
            ePoints = upvoters.size() - downvoters.size();
            return true;
        }
        return false;
    }

    /**
     * Checks whether an user has upvoted a specific enty
     * @param user The user who might have upvoted the entry
     * @return true, if the user upvoted the entry - false, otherwise
     */
    public boolean hasUpvoted( User user){

        if( upvoters.containsKey( user.getUid())){
            return true;
        }
        return false;
    }

    /**
     * Checks whether an user has upvoted a specific enty
     * @param user The user who might have upvoted the entry
     * @return true, if the user upvoted the entry - false, otherwise
     */
    public boolean hasDownvoted( User user){

        if( downvoters.containsKey( user.getUid())){
            return true;
        }
        return false;
    }

    /**
     * Gets the location ID of the wall
     * @return the ID
     */
    public int getLocID() {
        return loc.getId();
    }


}
