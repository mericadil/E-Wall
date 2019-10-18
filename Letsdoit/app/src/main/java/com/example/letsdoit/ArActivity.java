package com.example.letsdoit;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.example.letsdoit.EWallArFragment;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node.OnTapListener;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;


/**
 * The Ar Page
 * @author adil, ahmet
 * @version 1.0
 */
public class ArActivity extends AppCompatActivity implements Scene.OnUpdateListener {

    private EWallArFragment arFragment;
    private ArrayList<EWall> eWalls;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate( savedInstanceState);
        setContentView( R.layout.activity_camera);

        arFragment = ( EWallArFragment) getSupportFragmentManager().findFragmentById( R.id.fragment);
        if (arFragment != null) {
            arFragment.getArSceneView().getScene().addOnUpdateListener(this);
        }
    }

    /**
     * Sets up the database of EWalls and AugmentedImages
     * @param config
     * @param session the session
     */
    public void setUpDatabase( Config config, Session session) {

        // Set up data of EWalls
        eWalls = new ArrayList<>();

        eWalls.add( new EWall( "b_building", WallLocation.B_BUILDING, R.drawable.b_building));
        eWalls.add( new EWall ( "bilka", WallLocation.BILKA, R.drawable.bilka));
        eWalls.add( new EWall ( "library", WallLocation.LIBRARY, R.drawable.library));


        // Set up the image database.
        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);

        for ( EWall wall : eWalls) {
            aid.addImage( wall.imageName, wall.getBitmap( this.getResources()));
        }

        config.setAugmentedImageDatabase(aid);
    }

    /**
     * Searches for markers, called at the start of each frame
     * @param frameTime time passed since last frame
     */
    @Override
    public void onUpdate( FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        if ( frame != null) {
            Collection<AugmentedImage> images = frame.getUpdatedTrackables(AugmentedImage.class);

            for (AugmentedImage image : images) {
                if (image.getTrackingState() == TrackingState.TRACKING) {
                    for (EWall wall : eWalls) {
                        if (image.getName().equals(wall.imageName)) {
                            Anchor anchor = image.createAnchor(image.getCenterPose());

                            createWall(anchor, wall);
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a wall when a marker is spotted
     * @param anchor he anchor that anchors the 3d object
     * @param wall EWall object
     */
    private void createWall(Anchor anchor, EWall wall) {

        Uri uri = Uri.parse( "Wall.sfb");

        ModelRenderable.builder().setSource( this, uri)
                .build().thenAccept( new Consumer<ModelRenderable>() {
            @Override
            public void accept(ModelRenderable modelRenderable) {
                ArActivity.this.placeWall(modelRenderable, anchor, wall.id);
            }
        });
    }

    /**
     * Places the wall to the Ar Scene
     * @param modelRenderable renderable object of the wall
     * @param anchor the anchor that anchors the 3d object
     * @param wallId id of the wall placed to give information to content page
     */
    private void placeWall(ModelRenderable modelRenderable, Anchor anchor, int wallId) {

        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setRenderable(modelRenderable);
        ArActivity c = this;

        // Set the on tap listener to open the content page of the wall
        anchorNode.setOnTapListener(new OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                Intent intent = new Intent( c, PostPage.class);
                intent.putExtra( "wallID", wallId);
                c.startActivity( intent);
                // c.finish();
            }
        });
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }

    /**
     * An EWall class to store data about the 3d wall object
     */
    class EWall {

        int id;
        String imageName;
        int bitmapid;

        EWall( String imageName, int id, int bitmapid) {

            this.id = id;
            this.imageName = imageName;
            this.bitmapid = bitmapid;

        }

        /**
         * Generates the bitmap
         * @param resources resources of the activity which called this method
         * @return bitmap of the EWall
         */
        Bitmap getBitmap( Resources resources) {
            return BitmapFactory.decodeResource(
                    resources,
                    this.bitmapid
            );
        }
    }
}