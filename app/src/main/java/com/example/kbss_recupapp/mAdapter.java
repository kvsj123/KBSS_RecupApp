package com.example.kbss_recupapp;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;


public class mAdapter extends RecyclerView.Adapter<mAdapter.MyViewHolder> {

    Context context;


    Dictionary<String,Marker> list;






    public mAdapter(Context context, Dictionary<String,Marker> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Marker marker = Collections.list(list.elements()).get(position);
        holder.title.setText(marker.getTitel());
        holder.description.setText(marker.getOmschrijving());
        holder.lng.setText(marker.getLngBis());
        holder.lat.setText(marker.getLatBis());
        Glide.with(context).load(marker.getFoto()).into(holder.photo);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://kbssrecupapp-default-rtdb.europe-west1.firebasedatabase.app");
                DatabaseReference parentRef = database.getReference("Markers");

                String key = Collections.list(list.keys()).get(position);

                parentRef.child(key).removeValue();

                list.remove(key);
                notifyDataSetChanged();
            }
        });


        StorageReference storageRef = FirebaseStorage.getInstance("gs://kbssrecupapp.appspot.com/").getReference();
        StorageReference imageRef = storageRef.child("images");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public static  class  MyViewHolder extends RecyclerView.ViewHolder{

        TextView title,description,lng,lat;
        ImageView photo;
        Button delete;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            lng = itemView.findViewById(R.id.tvLng);
            lat = itemView.findViewById(R.id.tvLat);
            photo = itemView.findViewById(R.id.photoUpload);
            delete = (Button) itemView.findViewById(R.id.deleteBtn);

        }


    }
}
