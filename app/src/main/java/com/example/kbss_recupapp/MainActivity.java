package com.example.kbss_recupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button inlogregisterbtn;
    private EditText email, wachtwoord;
    private Button inloggen;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inlogregisterbtn = (Button) findViewById(R.id.inlogregiserbtn);
        inlogregisterbtn.setOnClickListener(this);

        inloggen = (Button) findViewById(R.id.inlogbtninloggen);
        inloggen.setOnClickListener(this);

        email = (EditText) findViewById(R.id.inloggebruikersnaam);
        wachtwoord = (EditText) findViewById(R.id.inlogwachtwoord);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch ((view.getId())){
            case R.id.inlogregiserbtn:
                startActivity(new Intent(this,RegisterActivity.class));
                break;

            case R.id.inlogbtninloggen:
                LogIn();
                break;
        }
    }

    private void LogIn(){
        String mail = email.getText().toString();
        String wachtw = wachtwoord.getText().toString();

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

        if(wachtw.length() < 6) {
            wachtwoord.setError("De wachtwoord moet minstens 6 karakters hebben!");
            wachtwoord.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(mail,wachtw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                }
                else{
                    Toast.makeText(MainActivity.this, "Inloggen mislukt!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}