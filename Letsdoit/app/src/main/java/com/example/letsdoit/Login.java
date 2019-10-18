package com.example.letsdoit;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    // properties
    TextView emailTV;
    Button loginButton;
    Button guestModeButton;
    Button newAccountButton;

    final String TAG = "mockprogramSignIn";
    String displayName;

    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    EditText mEmailText;
    EditText mPasswordText;

    boolean mLogonInProgress = false;


    // methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailText = (EditText)findViewById(R.id.userNameEditText);
        mPasswordText = (EditText)findViewById(R.id.passwordText);

        initFirebase();
        emailTV = (TextView)findViewById(R.id.emailText);

        loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmailText.getText().toString();
                final String password = mPasswordText.getText().toString();

                mLogonInProgress = false;
                loginUser( email, password);

                new CountDownTimer(1500, 100) {
                    public void onTick(long millisUntilFinished){

                    }
                    public void onFinish() {
                        Log.e(TAG, "mLogon" + mLogonInProgress);
                        if ( mLogonInProgress){
                            Intent options = new Intent( Login.this, Options.class);
                            options.putExtra("username", email);//get username
                            startActivity( options);

                        }
                        else{
                            //Toast.makeText( getApplicationContext(),"Wrong mail or password",
                                    //2000).show();
                        }
                    }
                }.start();


            }
        });

        guestModeButton = (Button)findViewById(R.id.guestModeBut);
        guestModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent guestOptions = new Intent( Login.this, GuestOptions.class);
                //startActivity( guestOptions);
            }
        });

        newAccountButton = (Button)findViewById(R.id.newAccountButton);
        newAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newAccount = new Intent( Login.this, NewAccount.class);
                startActivity( newAccount);
                finish();
            }
        });

    }

    /**
     * Login method for the user
     * @param email the email of the user
     * @param password the password of the user
     */
    private void loginUser(String email, String password) {
        OnCompleteListener<AuthResult> complete = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    mLogonInProgress = true;
                    Log.e(TAG, "mLogon" + mLogonInProgress);
                    Log.e(TAG, "SignIn : User logged on " + mLogonInProgress);
                    openOptionsPage();
                }
                else
                    Log.e(TAG, "SignIn : User log on response, but failed ");
            }
        };

        OnFailureListener failure = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"SignIn : Log on user failure");
            }
        };

        Log.e(TAG, "SignIn : Logging in : eMail [" + email + "] password [" + password + "]");
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(complete).addOnFailureListener(failure);
    }

    /**
     * Initialize firebase services
     */
    private void initFirebase() {

        FirebaseApp.initializeApp(this);
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance(mApp);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    displayName = user.getDisplayName();
                }
                else
                    Log.e(TAG, "SignIn : No Current user");
            }
        };
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    /**
     * Open the options page with username
     */
    private void openOptionsPage(){
        Intent options = new Intent( Login.this, Options.class);
        options.putExtra("username", mEmailText.getText().toString());//get username
        startActivity( options);
        finish();
    }
}
