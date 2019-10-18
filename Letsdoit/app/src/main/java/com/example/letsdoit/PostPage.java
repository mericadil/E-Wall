package com.example.letsdoit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The type Post page.
 */
public class PostPage extends AppCompatActivity {

    /**
     * The Tag.
     */
// properties
    final String TAG = "mockprogramSignIn";

    TextView wallName;
    ImageButton addPostButton;
    ImageButton sortByTimeButton;
    ImageButton sortByLikeButton;
    int currentWallID;
    User currentUser;
    boolean isPointSorted;

    LinearLayout postsLayout;
    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseFirestore db;
    CollectionReference entries;

    ArrayList<Entry> entryArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);
        entryArrayList = new ArrayList<Entry>();

        wallName = (TextView)findViewById(R.id.wallName);

        addPostButton = (ImageButton)findViewById(R.id.addPost);
        sortByTimeButton = (ImageButton)findViewById(R.id.sortTime);
        sortByLikeButton = (ImageButton)findViewById(R.id.sortLike);
        postsLayout = (LinearLayout)findViewById(R.id.post_layout);

        Intent i = getIntent();
        currentWallID = i.getIntExtra("wallID",-1);

        if ( currentWallID == WallLocation.B_BUILDING ) {
            wallName.setText("B Building wall");
        }
        else if(currentWallID== WallLocation.LIBRARY ){

            wallName.setText("Library wall");
        }
        else if(currentWallID == WallLocation.BILKA ) {
            wallName.setText("Bilka wall");
        }
        isPointSorted = false;

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPost = new Intent( PostPage.this, CreateEntry.class);
                newPost.putExtra("wallID", currentWallID);
                startActivity( newPost);
            }
        });

        sortByTimeButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    showEntriesByTime();
                                                }
                                            }
        );

        sortByLikeButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Log.e(TAG,"point");
                                                    showEntriesByPoints();
                                                }
                                            }
        );

        initFirebase();
        initAppUser();
    }

    /**
     * Initialize firebase services
     */
    private void initFirebase() {

        FirebaseApp.initializeApp( this);
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance(mApp);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

            }
        };
        mAuth.addAuthStateListener(mAuthStateListener);

        db = FirebaseFirestore.getInstance();
        entries = db.collection( "entries");

    }

    /**
     * Initialize firebase services for the users of app
     */
    private void initAppUser(){

        db.collection( "users").document( mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if( task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    currentUser = document.toObject( User.class);
                }
            }
        });
    }

    /**
     * Sort and show the entries according to the time they are written
     */
    public void showEntriesByTime(){

        entries.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if( task.isSuccessful()){
                    entryArrayList.clear();
                    for(DocumentSnapshot documentSnapshot : task.getResult())
                    {
                        Entry entry = documentSnapshot.toObject( Entry.class);
                        if(entry.getLocID() == currentWallID )
                            entryArrayList.add(entry);
                    }
                    sortByTime();
                }
            }
        });

    }

    /**
     * Sort and show the entries according to their ePoints
     */
    public void showEntriesByPoints(){

        entries.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if( task.isSuccessful()){
                    entryArrayList.clear();
                    for( DocumentSnapshot documentSnapshot : task.getResult())
                    {
                        Entry entry = documentSnapshot.toObject( Entry.class);
                        if(entry.getLocID() == currentWallID )
                            entryArrayList.add(entry);

                    }
                    sortByPoints();
                }
            }
        });
    }

    /**
     * Sorts the collection of entries according to the time they are written
     */
    public void sortByTime() {

        Collections.sort( entryArrayList, Entry.EntryTimeComparator);

        addListToView();
    }

    /**
     * Sorts the collection of entries according to their ePoints
     */
    public void sortByPoints() {
        Collections.sort( entryArrayList, Entry.EntryPointComparator);

        addListToView();
    }

    /**
     * Gets a random entry from a collection of entries
     * @return the collection of entries
     */
    public Entry getRandomEntry(){

        int index = (int) (Math.random() * entryArrayList.size());

        if( entryArrayList.get( index) != null)
            return entryArrayList.get( index);

        return new Entry();

    }

    /**
     * Show the entries on the screen acoording to the users preferences ie sorted by point, time etc.
     */
    public void addListToView() {
        postsLayout.removeAllViewsInLayout();
        Log.e(TAG,"point " + entryArrayList.size());
        for( int i = 0; i < entryArrayList.size(); i++ ) {
            final Entry currentEntry = entryArrayList.get(i);

            RelativeLayout postLayout = new RelativeLayout(this);

            TextView postTV = new TextView(this);
            postTV.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

            ImageButton upvote = new ImageButton(this);
            ImageButton downvote = new ImageButton(this);
            TextView epoint = new TextView(this);
            TextView name = new TextView(this);
            TextView date = new TextView(this);
            upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    db.collection( "entries").document( currentEntry.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Entry e = documentSnapshot.toObject( Entry.class);
                            e.addUpvoter( currentUser);

                            if(currentEntry.getId().equals( e.getId())){
                                db.collection("entries").document( currentEntry.getId()).set( e);
                            }
                        }
                    });
                    if( isPointSorted){
                        showEntriesByPoints();
                    }
                }
            });

            downvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    db.collection( "entries").document( currentEntry.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Entry e = documentSnapshot.toObject( Entry.class);
                            e.addDownvoter( currentUser);

                            if(currentEntry.getId().equals( e.getId())){
                                db.collection("entries").document( currentEntry.getId()).set( e);
                            }
                        }
                    });

                    if( isPointSorted){
                        showEntriesByPoints();
                    }

                }
            });

            RelativeLayout.LayoutParams upvoteRule = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams downvoteRule = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams nameRule = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams dateRule = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams epointRule = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            postTV.setText(entryArrayList.get(i).getText());
            postTV.setId(122);
            upvote.setImageResource(R.drawable.upvote_button);
            downvote.setImageResource(R.drawable.downvote_button);
            upvote.setId(124);
            downvote.setId(123);
            name.setId(125);
            name.setText(currentEntry.getShowName());
            date.setText(currentEntry.getDateStr());
            epoint.setText("ePoints: " + currentEntry.getePoints());

            postLayout.addView(postTV);
            downvoteRule.addRule(RelativeLayout.BELOW,postTV.getId());
            downvoteRule.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
            postLayout.addView(downvote,downvoteRule);
            upvoteRule.addRule(RelativeLayout.BELOW,postTV.getId());
            upvoteRule.addRule(RelativeLayout.LEFT_OF,downvote.getId());
            postLayout.addView(upvote,upvoteRule);
            nameRule.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
            nameRule.addRule(RelativeLayout.BELOW,downvote.getId());
            postLayout.addView(name, nameRule);
            dateRule.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
            dateRule.addRule(RelativeLayout.BELOW,name.getId());
            postLayout.addView(date, dateRule);
            epointRule.addRule(RelativeLayout.ABOVE,name.getId());
            epointRule.addRule(RelativeLayout.LEFT_OF,date.getId());
            postLayout.addView(epoint,epointRule);

            postsLayout.addView(postLayout);
        }
    }
}