package com.example.letsdoit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NewAccount extends AppCompatActivity {

    // properties
    final String TAG = "mockprogramSignIn";

    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    EditText mEmailEdit;
    EditText mPasswordEdit;
    EditText mConfirmPasswordEdit;
    EditText mDisplayName;

    TextView mRegisterText;
    String displayName;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        initFirebase();
        initDisplayControls();
        initListeners();
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

                if (user != null) {
                    displayName = user.getDisplayName();
                    Log.e(TAG, "SignIn : Valid current user : email [" + user.getEmail() + "] display name [" + mDisplayName + "]");
                }
                else
                    Log.e(TAG, "SignIn : No Current user");
            }
        };
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    /**
     * Initialize the instances of display controls
     */
    private void initDisplayControls() {

        mEmailEdit = (EditText) findViewById(R.id.emailEdit);
        mPasswordEdit = (EditText) findViewById(R.id.passwordEdit);
        mConfirmPasswordEdit = (EditText) findViewById(R.id.confirmPasswordEdit);
        mDisplayName = (EditText) findViewById(R.id.displayNameEdit);
        mDisplayName.setVisibility( View.GONE);

        mRegisterText = (TextView) findViewById(R.id.registerText);
    }

    /**
     * Initialize the listeners to register the user
     */
    private void initListeners() {

        mRegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmailEdit.getText().toString();
                password = mPasswordEdit.getText().toString();
                String confirmPassword = mConfirmPasswordEdit.getText().toString();
                displayName = mDisplayName.getText().toString();

                registerUser( email, password, displayName);
            }

        });
    }

    /**
     * Finishing sign-in activity
     */
    private void finishActivity() {
        Log.e(TAG, "Finishing SIGN IN activity");

        Intent returningIntent;
        returningIntent = new Intent(NewAccount.this, NameActivity.class);
        returningIntent.putExtra("password", password);
        setResult(RESULT_OK, returningIntent);
        returningIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(returningIntent);
        finish();
    }

    /**
     * Register the user with the given information
     * @param email the email of the user
     * @param password the password of the user
     * @param displayName the username of the user
     */
    private void registerUser(String email, String password, String displayName) {
        OnCompleteListener<AuthResult> complete = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Log.e(TAG, "SignIn : User registered ");
                    finishActivity();
                }
                else
                    Log.e(TAG, "SignIn : User registration response, but failed ");
            }
        };

        OnFailureListener failure = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"SignIn : Register user failure");
            }
        };

        Log.e(TAG, "SignIn : Registering : eMail [" + email + "] password [" + password + "] Display Name [" + displayName + "]");
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(complete).addOnFailureListener(failure);
    }
}
