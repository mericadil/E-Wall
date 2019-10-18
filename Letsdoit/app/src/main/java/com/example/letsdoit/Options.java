package com.example.letsdoit;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Options extends AppCompatActivity {
    TextView greeting;
    ImageButton logoutButton;

    String TAG = "username : ";

    ImageButton searchButton;
    EditText searchedUser;
    Button cameraButton;
    Button wallLocationButton;
    Button settingsButton;
    Button helpButton;
    boolean foundUser;

    String greetingText;
    String currentUserID;

    private FirebaseApp mApp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private com.google.firebase.auth.FirebaseUser FirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        FirebaseApp.initializeApp( this);

        initFirebase();

        Intent i = getIntent();
        greeting = (TextView)findViewById(R.id.greetingTextView);

        logoutButton = (ImageButton)findViewById(R.id.logoutBut);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                openLoginPage();
            }
        });

        searchedUser = (EditText)findViewById(R.id.searchUser);
        searchButton = (ImageButton)findViewById(R.id.searchBut);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchUserPage();
            }
        });

        cameraButton = (Button)findViewById(R.id.openCamButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraPage();
            }
        });

        wallLocationButton = (Button)findViewById(R.id.placeOfWalls);
        wallLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWallLocationPage();
            }
        });

        settingsButton = (Button)findViewById(R.id.settingsBut);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsPage();
            }
        });

        helpButton = (Button)findViewById(R.id.helpBut);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelpPage();
            }
        });
    }

    /**
     * Opens the login page using an intent
     */
    public void openLoginPage(){
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    /**
     * Opens the search user page so that users can search for their friends
     */
    public void openSearchUserPage(){

        foundUser = false;
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for( QueryDocumentSnapshot document : task.getResult()){
                        User user = document.toObject( User.class);
                        if( user.getUsername().equals( searchedUser.getText().toString())){
                            Intent searchUserPage = new Intent(Options.this, ViewProfile.class);
                            searchUserPage.putExtra("searchName", searchedUser.getText().toString());
                            startActivity(searchUserPage);
                            foundUser = true;
                        }
                        else{
                            Log.e( TAG, user.getUsername());
                        }
                    }
                if( !foundUser) {
                    Toast.makeText(Options.this, "User not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Opens camera page for ar activity
     */
    public void openCameraPage(){

        Intent cameraPage = new Intent( Options.this, ArActivity.class);
        startActivity( cameraPage);
    }

    /**
     * Opens the page that contains the wall location
     */
    public void openWallLocationPage(){
        Intent mapPage = new Intent( Options.this, Map.class);
        startActivity( mapPage);
    }

    /**
     * Opens the settings page
     */
    public void openSettingsPage(){
        Intent accountSetPage = new Intent( Options.this, AccountSettings.class);
        startActivity( accountSetPage);
    }

    /**
     * Opens the help page
     */
    public void openHelpPage(){
        Intent helpPage = new Intent( Options.this, Help.class);
        startActivity( helpPage);
    }

    /**
     * Initialize firebase services
     */
    private void initFirebase(){

        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance( mApp);

        FirebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser = firebaseAuth.getCurrentUser();

                if (FirebaseUser != null) {
                    currentUserID = FirebaseUser.getUid();
                    initUser();
                }

            }
        };
        mAuth.addAuthStateListener(mAuthStateListener);

    };

    /**
     * Initialize the user
     */
    private void initUser(){

        db.collection( "users").document( currentUserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                currentUser = documentSnapshot.toObject( User.class);
                greetingText = "Hello " + currentUser.getUsername();
                greeting.setText(greetingText);
            }
        });
    }
}
