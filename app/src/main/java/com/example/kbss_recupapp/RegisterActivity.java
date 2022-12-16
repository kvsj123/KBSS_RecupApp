package com.example.kbss_recupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    EditText gebruikersnaam, email, wachtwoord;
    Button btninschrijven, btninloggen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://kbssrecupapp-default-rtdb.europe-west1.firebasedatabase.app/");


        gebruikersnaam = (EditText) findViewById(R.id.gebruikersnaam);
        email = (EditText) findViewById(R.id.email);
        wachtwoord = (EditText) findViewById(R.id.wachtwoord);


        btninschrijven = (Button) findViewById(R.id.btninschrijven);
        btninschrijven.setOnClickListener(this);
        btninloggen = (Button) findViewById(R.id.btninloggen);
        btninloggen.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btninschrijven:
                inschrijven();
                break;
            case R.id.btninloggen:
                startActivity(new Intent(this,MainActivity.class));
                break;
        }
    }

    private void inschrijven(){
        String gebruikersn = gebruikersnaam.getText().toString();
        String mail = email.getText().toString();
        String wachtw = wachtwoord.getText().toString();


        if(gebruikersn.isEmpty()){
            gebruikersnaam.setError("Vul de gebruikersnaam in!");
            gebruikersnaam.requestFocus();
            return;
        }

        if(mail.isEmpty()){
            email.setError("Vul de email in!");
            email.requestFocus();
            return;
        }

        if(wachtw.isEmpty()){
            wachtwoord.setError("Vul de wachtwoord in!");
            wachtwoord.requestFocus();
            return;
        }


        if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            email.setError("Vul een geldig email in!");
            email.requestFocus();
            return;
        }

        if(wachtw.length() < 6){
            wachtwoord.setError("De wachtwoord moet minstens 6 karakters hebben!");
            wachtwoord.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(mail,wachtw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Gebruiker gebruiker1 = new Gebruiker(gebruikersn, mail);

                        database.getReference("Gebruikers")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(gebruiker1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Account aangemaakt!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Probeer opnieuw!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                    else{
                        Toast.makeText(RegisterActivity.this, " opnieuw!", Toast.LENGTH_SHORT).show();
                    }

                }
            });


    }
}