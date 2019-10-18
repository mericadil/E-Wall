package com.example.letsdoit;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AccountSettings extends AppCompatActivity {

    // properties
    private FirebaseApp mApp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private FirebaseUser user;
    private User appUser;
    private CollectionReference entriesRef;
    private DocumentReference entryRef;

    String currentUserID;
    String userPassword;

    private EditText mNameText;
    private EditText mUsernameText;
    private Button mSaveButton;
    private EditText mEmailText;
    private EditText mPasswordText;
    private EditText mConfirmText;
    private Button mBackButton;
    private Button mDeleteButton;
    private Button mFriendsButton;

    private String TAG = "AccountSettings";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        FirebaseApp.initializeApp( this);
        initFirebase();
        initDisplayControls();
        initFirebaseUser();
    }

    /**
     * Initialize firebase features
     */
    private void initFirebase() {
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance( mApp);
        db = FirebaseFirestore.getInstance();

    }

    /**
     * Initialize firebase features for the user
     */
    private void initFirebaseUser()
    {
        user = mAuth.getCurrentUser();
        currentUserID = user.getUid();
        userRef = db.collection( "users").document( currentUserID);

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                appUser = documentSnapshot.toObject( User.class);
                userPassword = appUser.getPassword();
                Log.e( TAG, userPassword);

            }
        });
        entriesRef = db.collection("entries");
    }

    /**
     * Initialize the views of display controls
     */
    private void initDisplayControls()
    {
        mNameText = (EditText) findViewById( R.id.nameText);
        mUsernameText = (EditText) findViewById( R.id.usernameText);
        mPasswordText = (EditText) findViewById( R.id.passwordText);
        mEmailText = (EditText) findViewById( R.id.emailText);
        mConfirmText = (EditText) findViewById( R.id.confirmText);

        mSaveButton = (Button) findViewById( R.id.saveButton);
        mBackButton = (Button) findViewById( R.id.backButton);
        mDeleteButton =(Button) findViewById( R.id.deleteButton);
        mFriendsButton =(Button) findViewById( R.id.myfriendsButton);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });


        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnBack();
            }
        });

        mFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFriends();
            }
        });

    }

    private void showFriends()
    {
        // shoes the friends of user
    }

    private void returnBack()
    {
        // return to the earlier intent
    }

    /**
     * Deletes the account of the user and the entries
     */
    private void deleteAccount() {

        AlertDialog.Builder popup = new AlertDialog.Builder( AccountSettings.this);
        popup.setMessage( "Do you want to delete your account?").setCancelable( false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        userRef.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });

                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User account deleted.");
                                        }
                                    }
                                });

                        // delete entries of this user
                        finish();
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = popup.create();
        alert.setTitle( "Deleting Account");
        alert.show();
    }

    /**
     * Save the changes that is made to the user's account settıng such as password, name, email etc.
     */
    private void saveChanges() {

        if ( mPasswordText.getText().toString().length() > 6
                && mConfirmText.getText().toString().equals( mPasswordText.getText().toString()))
        {
            user.updatePassword( mPasswordText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User password updated.");
                            }
                        }
                    });
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    appUser = documentSnapshot.toObject( User.class);
                    appUser.setPassword( mPasswordText.getText().toString());
                }
            });

            userRef.set( appUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.e( TAG, "Successful password!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e( TAG, "Unsuccessful password!");
                }
            });
        }

        if( mNameText.getText().toString().length() > 6 )
        {
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    appUser = documentSnapshot.toObject( User.class);
                    appUser.setName( mNameText.getText().toString() );
                }
            });

            userRef.set( appUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.e( TAG, "Successful name!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e( TAG, "Unsuccessful name!");
                }
            });

        }

        if( mUsernameText.getText().toString().length() > 6)
        {
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    appUser = documentSnapshot.toObject( User.class);
                    appUser.setUsername( mUsernameText.getText().toString() );
                }
            });

            userRef.set( appUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.e( TAG, "Successful username!");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e( TAG, "Unsuccessful username!");
                        }
                    });
        }

        if( mEmailText.getText().toString().length() > 10 && mEmailText.getText().toString().contains( "@"))
        {
            user.updateEmail(mEmailText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User email address updated.");
                            }
                        }
                    });
        }

        entriesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Entry entry = document.toObject(Entry.class);
                        entryRef = db.collection("entries").document( entry.getId());
                        if( entry.getUser().equals( appUser)){
                            entry.setUser( appUser);
                            entryRef.set( entry);

                        }
                    }
                }
            }
        });

    }

}
