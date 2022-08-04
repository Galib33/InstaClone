package com.galib.instaclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.galib.instaclone.databinding.ActivityMainBinding;
import com.galib.instaclone.databinding.ActivityScrollBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ScrollActivity extends AppCompatActivity {
    private ActivityScrollBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    ArrayList<Details> detailsArrayList;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityScrollBinding.inflate(LayoutInflater.from(this));
        View view=binding.getRoot();
        setContentView(view);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        detailsArrayList=new ArrayList<>();
        Intent intent=getIntent();
        if(intent.getStringExtra("case").matches("itself")){
            getmydata();
        }else{
            getalldata();
        }
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new Adapter(detailsArrayList);
        binding.recyclerView.setAdapter(adapter);
    }
    public void getmydata(){
        String collectionName=user.getEmail()+"'s Posts";

        firebaseFirestore.collection("Posts").whereEqualTo("Email",user.getEmail()).orderBy("Time", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(ScrollActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if(value!=null){
                    for (DocumentSnapshot document : value.getDocuments()){
                        String email=(String) document.get("Email");
                        String uriTaken=(String) document.get("ImageURL");
                        String comment=(String) document.get("Comment");
                        Details details=new Details(email,uriTaken,comment);
                        detailsArrayList.add(details);

                    }
                    adapter.notifyDataSetChanged();

                }
            }
        });
    }
    public void getalldata(){

        firebaseFirestore.collection("Posts").orderBy("Time", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(ScrollActivity.this, "Error Detected:"+ error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                if(value!=null){
                    for (DocumentSnapshot document : value.getDocuments()){
                        String email=(String) document.get("Email");
                        String uriTaken=(String) document.get("ImageURL");
                        String comment=(String) document.get("Comment");
                        Details details=new Details(email,uriTaken,comment);
                        detailsArrayList.add(details);

                    }
                    adapter.notifyDataSetChanged();

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.instamenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.idtoupload){
            Intent intent=new Intent(this,UploadActivity.class);
            startActivity(intent);
        }else if(item.getItemId()==R.id.logoutid){
            auth.signOut();
            Intent intent=new Intent(ScrollActivity.this,MainActivity.class);
            finish();
            startActivity(intent);

        }else{
            Intent intent=getIntent();
            intent.putExtra("case","itself");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}