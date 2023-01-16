package com.example.kbss_recupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

public class MarkerList extends AppCompatActivity {

    RecyclerView recyclerView;
    mAdapter adapter;
    Dictionary<String,Marker> list;
    DatabaseHelper firebaseHelper = DatabaseHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_list);

        recyclerView = findViewById(R.id.markerList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new Hashtable<String,Marker>();
        adapter = new mAdapter(this,list);
        recyclerView.setAdapter(adapter);



        firebaseHelper.addValueEventListener(new DatabaseHelper.FirebaseCallback() {
            @Override
            public void onCallback(DataSnapshot snapshot) {

                for(DataSnapshot markerSnapshot : snapshot.getChildren()){
                    Marker marker = markerSnapshot.getValue(Marker.class);
                    list.put(markerSnapshot.getKey(),marker);
                    adapter.notifyItemInserted(list.size()-1);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_markerlist, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_markerList:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}