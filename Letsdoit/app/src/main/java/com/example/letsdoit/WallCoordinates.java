package com.example.letsdoit;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * The class where the locations are stored
 */
public class WallCoordinates
{
    ArrayList<WallLocation> allWallCoordinates;

    /**
     * Wall coordinates
     */
    protected WallCoordinates()
    {
        allWallCoordinates = new ArrayList<>();
        // location of Ihsan Dogramaci wall (ID wall)
        allWallCoordinates.add(new WallLocation("B Building wall", 39.868732,32.748743, WallLocation.B_BUILDING));
        // location of Bilka wall (Bilka wall)
        allWallCoordinates.add(new WallLocation("Bilka wall",39.864959,32.747579, WallLocation.BILKA));
        // location of MayFest wall (MayFest wall)
        allWallCoordinates.add(new WallLocation("Library wall",39.869992,32.749438,WallLocation.LIBRARY));
    }

    //methods

    /**
     * Gets the latitude of the object at the index i
     * @param i the index
     * @return the index
     */
    public double getXCoordinate(int i)
    {
        return this.allWallCoordinates.get(i).getLatitude();
    }

    /**
     * Gets the longtitude of the object at the index i
     * @param i the index
     * @return the index
     */
    public double getYCoordinate(int i)
    {
        return this.allWallCoordinates.get(i).getLongitude();
    }

    /**
     * Wall Location Method
     * @param i the index
     * @return the object of Wall Location
     */
    public WallLocation returnPoint( int i) {
        return allWallCoordinates.get(i);
    }

}


