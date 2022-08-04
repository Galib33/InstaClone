package com.galib.instaclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.galib.instaclone.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(LayoutInflater.from(this));
        View view=binding.getRoot();
        setContentView(view);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        if(user!= null){
            Intent intent=new Intent(MainActivity.this,ScrollActivity.class);
            intent.putExtra("case","new");
            finish();
            startActivity(intent);
        }

    }

    public void signIn(View view){
        if(binding.editEmail.getText().toString().matches("") || binding.editPassword.getText().toString().matches("")){
            Toast.makeText(this, "E-mail or Password is empty!", Toast.LENGTH_SHORT).show();
        }else{
            auth.signInWithEmailAndPassword(binding.editEmail.getText().toString(),binding.editPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent=new Intent(MainActivity.this,ScrollActivity.class);
                    intent.putExtra("case","new");
                    finish();
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }



    }

    public void signUp(View view){
        if(binding.editEmail.getText().toString().matches("") || binding.editPassword.getText().toString().matches("")){
            Toast.makeText(this, "E-mail or Password is empty!", Toast.LENGTH_SHORT).show();
        }else{
            auth.createUserWithEmailAndPassword(binding.editEmail.getText().toString(),binding.editPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent=new Intent(MainActivity.this,ScrollActivity.class);
                    intent.putExtra("case","new");
                    finish();
                    startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        


    }






}