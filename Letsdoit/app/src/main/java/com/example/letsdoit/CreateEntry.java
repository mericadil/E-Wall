package com.example.letsdoit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateEntry extends AppCompatActivity {


    // properties
    final String TAG = "mockprogramName";

    private FirebaseApp mApp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private FirebaseUser FirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private EditText mTextField;
    private Button mSendButton;
    private Entries entries;

    private Entry addedEntry;
    private WallLocation w;
    private String text;
    private int nameVisibility;
    private int wallID;

    private RadioButton mUsernameButton;
    private RadioButton mNameButton;
    private RadioButton mAnonButton;

    private String currentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_entry);

        initFirebase();
        initFirebaseUser();
        initDisplayControls();

        Intent i = getIntent();
        wallID = i.getIntExtra("wallID",-1);

        if ( wallID == WallLocation.B_BUILDING ) {
            w = new WallLocation("B Building wall", 39.868732, 32.748743, wallID);
        }
        else if(wallID== WallLocation.LIBRARY ){
            w = new WallLocation("Library wall", 39.869992, 32.749438, wallID);
        }
        else if(wallID == WallLocation.BILKA ) {
            w = new WallLocation("Bilka wall",39.864959,32.747579, wallID);
        }
        else {
            //w = null;
            w = new WallLocation("Bilka wall",39.864959,32.747579, wallID);
        }

    }

    /**
     * Initialize the views of display controls
     */
    private void initDisplayControls() {
        mTextField = (EditText) findViewById( R.id.textField);
        mNameButton = (RadioButton) findViewById( R.id.nameButton);
        mUsernameButton = (RadioButton) findViewById( R.id.usernameButton);
        mAnonButton = (RadioButton) findViewById( R.id.anonButton);
        mSendButton = (Button) findViewById( R.id.sendButton);

        mTextField.setVisibility(View.VISIBLE);
        mNameButton.setVisibility( View.VISIBLE);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEntry();
            }
        });
    }

    /**
     * Initialize firebase features for the user
     */
    private void initFirebaseUser() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser = firebaseAuth.getCurrentUser();

                if (FirebaseUser != null)
                {
                    currentUserID = FirebaseUser.getUid();
                    Toast.makeText(CreateEntry.this, currentUserID, Toast.LENGTH_SHORT).show();
                }
                else
                    Log.e(TAG, "SignIn : No Current user");
            }
        };
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    /**
     * Initialize firebase instances
     */
    private void initFirebase() {
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance( mApp);


        FirebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        entries = new Entries();

    }

    /**
     * Creates an entry
     * @return true, if the entry is created - false, if it is not created due to an error
     */
    private boolean createEntry() {

        userRef = db.collection("users").document( currentUserID);

        if ( mTextField.getText().toString().length() > 0 && mTextField.getText().toString().length() < Entry.CHAR_LIMIT)
            text = mTextField.getText().toString();
        else {
            Toast.makeText(this, "Please check your text length.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ( mNameButton.isChecked()){
            nameVisibility = Entry.NAME;
        }

        else if ( mUsernameButton.isChecked()){
            nameVisibility = Entry.USERNAME;
        }

        else {
            nameVisibility = Entry.ANON;
        }

        if( userRef != null) {
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        User user;

                        if (document != null) {
                            user = document.toObject(User.class);
                            Toast.makeText(CreateEntry.this, "" + user.getUsername(), Toast.LENGTH_SHORT).show();


                            if ( user != null){
                                readData(new MyCallBack() {
                                    @Override
                                    public void onCallBack(User user) {
                                        addedEntry = new Entry( user, text, w, nameVisibility);
                                        entries.addEntry(addedEntry);

                                    }
                                }, userRef);

                            }
                        } else {
                            Log.d(TAG, "No such element");
                        }
                        finish();
                    } else {
                        Log.d(TAG, "get Failed with ", task.getException());
                    }

                }
            });
        }


        return true;
    }

    /**
     * read the data written in a section
     * @param myCallBack MyCallBack object
     * @param ref Reference to the document
     */
    public void readData(final MyCallBack myCallBack, DocumentReference ref) {
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                myCallBack.onCallBack(user);
            }
        });
    }
}
