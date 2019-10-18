package com.example.letsdoit;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The type Entries.
 */
public class Entries implements Serializable {

    // properties
    private FirebaseFirestore db;
    private static String TAG = "entries";
    private Entry outputEntry;

    // constructors
    /**
     * Instantiates a new Entries.
     */
    public Entries(){

        db = FirebaseFirestore.getInstance();
    }

    // methods
    /**
     * Add entry.
     * @param entry the entry
     */
    public void addEntry( Entry entry){
        db.collection("entries").document(entry.getId())
                .set(entry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    /**
     * Get entry entry.
     * @param id the id
     * @return the entry
     */
    public Entry getEntry( String id){
        DocumentReference docRef = db.collection("entries").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                outputEntry  = documentSnapshot.toObject(Entry.class);
            }
        });
        return outputEntry;

    }
}
