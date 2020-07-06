package com.example.whatsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST =21 ;
    private Button buttonupdateSetting;
private EditText editTextusername,editTextststus;
private CircleImageView circleImageView;
private DatabaseReference databaseReference;
private FirebaseAuth firebaseAuth;
private StorageReference profileReference,Rootreference;
private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        firebaseAuth=FirebaseAuth.getInstance();
        profileReference= FirebaseStorage.getInstance().getReference().child("ProfileImage");
        databaseReference= FirebaseDatabase.getInstance().getReference();
       buttonupdateSetting = (Button)findViewById(R.id.updatesetting);
       Rootreference=FirebaseStorage.getInstance().getReference();
        circleImageView=(CircleImageView)findViewById(R.id.profile_image);
        editTextststus=(EditText)findViewById(R.id.status);
        editTextusername=(EditText)findViewById(R.id.username);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose();
            }
        });
        buttonupdateSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatesetting();
            }
        });
        retriveuserinfo();
    }
    public void
    retriveuserinfo(){
        String userid=firebaseAuth.getCurrentUser().getUid();
databaseReference.child("Users").child(userid).addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image")){
 String name=dataSnapshot.child("name").getValue().toString();
            String ststus=dataSnapshot.child("status").getValue().toString();
            String profileimage=dataSnapshot.child("image").getValue().toString();
          //  Glide.get(getApplicationContext()).load("http://goo.gl/gEgYUd").into(imageView);
            Picasso.get().load(profileimage).into(circleImageView);
            editTextststus.setText(ststus);
            editTextusername.setText(name);

        }
        if (dataSnapshot.exists() && dataSnapshot.hasChild("name")){
            String name=dataSnapshot.child("name").getValue().toString();
            String ststus=dataSnapshot.child("status").getValue().toString();
            editTextststus.setText(ststus);
            editTextusername.setText(name);
            editTextusername.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
    }
    public void choose(){
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK){
            uri=data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                circleImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final String currentUserid=firebaseAuth.getCurrentUser().getUid();
            final StorageReference filePath=profileReference.child(currentUserid+".jpg");
            filePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url=uri.toString();
                                databaseReference.child("Users").child(currentUserid).child("image").setValue(url);
                                Toast.makeText(getApplicationContext(),"sucessful uploded"+url,Toast.LENGTH_LONG).show();
                            }
                        }); }
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Toast.makeText(getApplicationContext()," uploded failed",Toast.LENGTH_LONG).show();
                }
            });
        }

    }

   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
*/
    public void updatesetting(){
        String username=editTextusername.getText().toString();
        String userstatus=editTextststus.getText().toString();
        if (username.isEmpty()){
            editTextusername.setError("Please enter your username");
            editTextusername.requestFocus();
            return;
        }
        String userid=firebaseAuth.getCurrentUser().getUid();
      HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("uid",userid);
        hashMap.put("name",username);
        hashMap.put("status",userstatus);
        databaseReference.child("Users").child(userid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
           Toast.makeText(getApplicationContext(),"Uploded",Toast.LENGTH_LONG).show();
            }}
        });
finish();
    }
}
