package com.example.letsdoit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewProfile extends AppCompatActivity {

    // properties
    /*
        profile page
     */

    final String TAG = "mockprogramSignIn";

    TextView name;
    TextView username;
    LinearLayout postsLayout;
    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseFirestore db;
    ArrayList<Entry> entryArrayList;
    Button followButton;

    User currentUser;
    User profileUser;
    DocumentReference currentUserRef;
    DocumentReference profileUserRef;
    CollectionReference users;
    String currentUserID;
    String profileUserID;
    String searchedUsername;
    ArrayList<Entry> userEntries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        postsLayout = (LinearLayout)findViewById(R.id.post_layout);
        entryArrayList = new ArrayList<Entry>();

        Intent i = getIntent();
        searchedUsername = i.getStringExtra("searchName");

        name = (TextView)findViewById(R.id.name);

        username =  (TextView)findViewById(R.id.username);

        initFirebase();

        friendEntries();

        followButton = (Button)findViewById(R.id.follow_button);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserFriend();
            }
        });

    }

    private void initFirebase(){

        FirebaseApp.initializeApp( this);
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance(mApp);

        db = FirebaseFirestore.getInstance();

        currentUserID = mAuth.getCurrentUser().getUid();

        currentUserRef = db.collection("users").document( currentUserID);
        currentUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject( User.class);
            }
        });

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for( QueryDocumentSnapshot document : task.getResult()){
                    User user = document.toObject( User.class);
                    if( user.getUsername().equals( searchedUsername)){
                        profileUser = user;
                        profileUserRef = db.collection("users").document(profileUser.getUid());
                        setPage();
                    }
                }
            }
        });
    }

    private void addUserFriend(){

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if( user.getUsername().equals( searchedUsername)) {
                            profileUser = user;

                            if (!profileUser.isAFriend(currentUser)) {

                                profileUser.addFriend(currentUser);
                                currentUser.addFriend(profileUser);
                                currentUserRef.set(currentUser);
                                profileUserRef.set(profileUser);

                            }
                        }
                    }
                }
            }
        });

    }

    public void friendEntries(){

        entryArrayList.clear();
        db.collection( "entries").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if( task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Entry entry = document.toObject( Entry.class);
                        if( profileUser != null){
                            if( entry.getUser().equals( profileUser)
                                    && entry.getViewChoice() != Entry.ANON)
                                entryArrayList.add( entry);
                            Log.e( TAG, entry.getText());
                        }
                    }
                    addListToView();
                }
            }
        });
    }

    public void addListToView() {
        postsLayout.removeAllViewsInLayout();
        Log.e(TAG, "point " + entryArrayList.size());
        for (int i = 0; i < entryArrayList.size(); i++) {
            final Entry currentEntry = entryArrayList.get(i);

            RelativeLayout postLayout = new RelativeLayout(this);

            TextView postTV = new TextView(this);
            ImageButton upvote = new ImageButton(this);
            ImageButton downvote = new ImageButton(this);
            TextView name = new TextView(this);
            TextView date = new TextView(this);

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

            postTV.setText(entryArrayList.get(i).getText());
            upvote.setImageResource(R.drawable.upvote_button);
            downvote.setImageResource(R.drawable.downvote_button);
            downvote.setId(10);
            name.setText(currentEntry.getShowName());
            name.setId(20);
            date.setText(currentEntry.getDateStr());

            postLayout.addView(postTV);
            downvoteRule.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            postLayout.addView(downvote, downvoteRule);
            upvoteRule.addRule(RelativeLayout.LEFT_OF, downvote.getId());
            postLayout.addView(upvote, upvoteRule);
            nameRule.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            nameRule.addRule(RelativeLayout.BELOW, downvote.getId());
            postLayout.addView(name, nameRule);
            dateRule.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            dateRule.addRule(RelativeLayout.BELOW, name.getId());
            postLayout.addView(date, dateRule);


            postsLayout.addView(postLayout);
        }
    }

    public void setPage(){
        name.setText( profileUser.getName());
        username.setText( profileUser.getUsername());

    }

}
