package com.example.kbss_recupapp;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatabaseHelper {
    private static DatabaseHelper instance;
    private DatabaseReference mDatabase;
    private ValueEventListener valueEventListener;


    public interface FirebaseCallback {
        void onCallback(DataSnapshot dataSnapshot);
    }

    private DatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance("https://kbssrecupapp-default-rtdb.europe-west1.firebasedatabase.app").getReference("Markers");
    }

    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    public void addValueEventListener(final FirebaseCallback callback){
        valueEventListener =  mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onCallback(dataSnapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    public void removeEventListener(){
        mDatabase.child("data").removeEventListener(valueEventListener);
    }

}
