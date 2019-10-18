package com.example.letsdoit;

import java.io.Serializable;

/**
 * The type Wall location.
 */
public class WallLocation implements Serializable {

    // properties
    public static final int B_BUILDING = 0;
    public static final int LIBRARY = 1;
    public static final int BILKA = 2;

    private int id;
    private String name;
    private double longitude;
    private double latitude;

    // constructors
    /**
     * Instantiates a new Wall location.
     */
    public WallLocation(){
        // for database purposes
    }

    /**
     * Instantiates a new Wall location.
     * @param name      the name
     * @param longitude the longitude
     * @param latitude  the latitude
     */public WallLocation( String name, double longitude, double latitude, int id){
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
    }

    // methods

    /**
     * Gets the ID.
     * @return the ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID.
     * @param id the ID of the wall
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets name.
     * @return name the name of the wall
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
     * Gets longitude.
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets longitude.
     * @param longitude the longtitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets latitude.
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets latitude.
     * @param latitude the latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

}

