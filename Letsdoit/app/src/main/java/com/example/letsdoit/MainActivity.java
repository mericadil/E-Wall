/**
 * This is the beginning of our program
 * @author Melike Fatma Aydogan
 * @version
 */
package com.example.letsdoit;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;

/**
 * Main Activity class where the application begins
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp( this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new CountDownTimer(2000, 100) {
            public void onTick(long millisUntilFinished){

            }
            public void onFinish() {
                openLoginPage();
            }
        }.start();

    }

    /**
     * Starts the app by opening login page
     */
    public void openLoginPage(){
        Intent ewall = new Intent(MainActivity.this, Login.class);
        startActivity(ewall);
        finish();
    }
}
