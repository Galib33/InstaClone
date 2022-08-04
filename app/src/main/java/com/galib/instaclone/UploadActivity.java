package com.galib.instaclone;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.galib.instaclone.databinding.ActivityMainBinding;
import com.galib.instaclone.databinding.ActivityUploadBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {
    private ActivityUploadBinding binding;
    ActivityResultLauncher<Intent> galleryLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri urifromgallery;
    FirebaseAuth auth;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseUser user;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityUploadBinding.inflate(LayoutInflater.from(this));
        View view=binding.getRoot();
        setContentView(view);
        registerforlaunchers();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();

    }

    public void tapImage(View view){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"I need permission",Snackbar.LENGTH_INDEFINITE).setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
               //permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

            }
        }else{
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //go to gallery
            galleryLauncher.launch(intent);
        }

    }

    public void upload(View view){
        if(urifromgallery != null && !binding.editComment.getText().toString().matches("")){
            UUID uuid;
            uuid=UUID.randomUUID();
            String forChild="images/"+uuid+".jpg";
            storageReference.child(forChild).putFile(urifromgallery).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //take imageUrl to database process
                    StorageReference reference=firebaseStorage.getReference(forChild);
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String uriTaken=uri.toString();
                            String comment=binding.editComment.getText().toString();
                            String email=user.getEmail();
                            HashMap<String,Object> objectHashMap=new HashMap<>();
                            objectHashMap.put("Email",email);
                            objectHashMap.put("ImageURL",uriTaken);
                            objectHashMap.put("Comment",comment);
                            objectHashMap.put("Time", FieldValue.serverTimestamp());
                            String collectionName=user.getEmail()+"'s Posts";
                            firebaseFirestore.collection("Posts").add(objectHashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(UploadActivity.this, "DONE!", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(UploadActivity.this,ScrollActivity.class);
                                    intent.putExtra("case","new");
                                    finish();
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            /*firebaseFirestore.collection(collectionName).add(objectHashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(UploadActivity.this, "DONE!", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(UploadActivity.this,ScrollActivity.class);
                                    intent.putExtra("case","new");
                                    finish();
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });*/




                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "Image or Comment are is null!", Toast.LENGTH_SHORT).show();
        }





    }
    public void registerforlaunchers(){
        galleryLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                    if(result.getData()!=null){
                        urifromgallery=result.getData().getData();
                        binding.imageView.setImageURI(urifromgallery);
                    }
                }else{
                    Toast.makeText(UploadActivity.this, "Nothing choosen!", Toast.LENGTH_SHORT).show();
                }
            }
        });



        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //gallerylauncher
                    galleryLauncher.launch(intent);
                }else{
                    Toast.makeText(UploadActivity.this, "I need permission!!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}