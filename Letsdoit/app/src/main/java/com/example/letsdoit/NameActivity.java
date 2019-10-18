package com.example.letsdoit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Name Activity Class stores the names of the features
 */
public class NameActivity extends AppCompatActivity {

    final String TAG = "mockprogramName";

    private FirebaseApp mApp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;

    private EditText mNameText;
    private EditText mUsernameText;
    private Button mNameButton;
    private String password;

    String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        Log.e( TAG, "Starting up NameActivity");
        Bundle extras = getIntent().getExtras();

        if( extras != null){
            password = extras.getString( "password");
        }

        Log.e( TAG, password);

        initDisplayControls();
        initFirebase();
    }

    /**
     * Initialize firebase services
     */
    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance( mApp);
        currentUserID = mAuth.getCurrentUser().getUid();

        db = FirebaseFirestore.getInstance();
    }

    /**
     * Initialize the instances of display control
     */
    private void initDisplayControls() {
        mNameText = (EditText) findViewById( R.id.nameText);
        mNameButton = (Button) findViewById( R.id.createButton);
        mUsernameText = (EditText) findViewById( R.id.usernameText);

        mNameText.setVisibility(View.VISIBLE);
        mNameButton.setVisibility( View.VISIBLE);
        mUsernameText.setVisibility( View.VISIBLE);

        mNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setName();
            }
        });

    }

    /**
     * Sets the name of the user with the given name in the sign up page
     */
    private void setName(){

        String name = mNameText.getText().toString();
        String username = mUsernameText.getText().toString();
        String uid = mAuth.getCurrentUser().getUid();

        User newUser = new User( name, username, uid, password);

        if (TextUtils.isEmpty( name)){
            Toast.makeText( this, "Please type your name.", Toast.LENGTH_SHORT).show();
        }
        else {

            db.collection( "users").document(uid).set( newUser);

            SendUserToOTypeActivity();
        }
    }

    /**
     * Sends to user to the specified activity ie the main menu
     */
    private void SendUserToOTypeActivity() {
        Intent mainIntent = new Intent( NameActivity.this, Options.class);
        mainIntent.putExtra("username",mUsernameText.getText().toString());
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity( mainIntent);
        finish();
    }
}

