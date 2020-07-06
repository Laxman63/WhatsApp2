package com.example.whatsapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActiivty extends AppCompatActivity {
private String messageReciverid,username,recivedimage,Messagesenderid;
private TextView textViewusername,textViewlastseen;
private CircleImageView circleImageViewuser;
private Toolbar toolbar;
private int totalfriends;
private DatabaseReference rootreference;
private EditText editTextmessage;
private FirebaseAuth firebaseAuth;
ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_actiivty);
        messageReciverid=getIntent().getExtras().get("visit_id").toString();
        username=getIntent().getExtras().get("username").toString();
        recivedimage=getIntent().getExtras().get("profileurl").toString();
firebaseAuth=FirebaseAuth.getInstance();
Messagesenderid=firebaseAuth.getCurrentUser().getUid();
rootreference= FirebaseDatabase.getInstance().getReference();
        intializeController();
        textViewusername.setText(username);
        textViewlastseen.setText("last seen");
        Picasso.get().load(recivedimage).into(circleImageViewuser);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }
    public void intializeController(){
        toolbar=(Toolbar)findViewById(R.id.chatappbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
       actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarview=layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarview);
        textViewlastseen=(TextView)findViewById(R.id.lastseen);
        textViewusername=(TextView)findViewById(R.id.customname);
        circleImageViewuser=(CircleImageView)findViewById(R.id.circlecustomchat);
        imageView=(ImageView)findViewById(R.id.sendbutton);
        editTextmessage=(EditText)findViewById(R.id.chatwrite);

    }
    private void sendMessage(){
        String message=editTextmessage.getText().toString().trim();
        if (message.isEmpty()){
            Toast.makeText(getApplicationContext(),"please enter your message",Toast.LENGTH_LONG).show();
        }
        else {
            String messageSenderreference="Message"+"/"+Messagesenderid+"/"+messageReciverid;
            String messagereciverid="Message"+"/"+messageReciverid+"/"+Messagesenderid;
            DatabaseReference messageuniquekey=rootreference.child("Mesage").child(Messagesenderid)
                    .child(messagereciverid).push();
            String mesagepushid=messageuniquekey.getKey();
            Map mesagetextbody=new HashMap();
            mesagetextbody.put("message",message);
            mesagetextbody.put("type","text");
            mesagetextbody.put("from",Messagesenderid);
            Map messageBodydetails=new HashMap();
            messageBodydetails.put(messageSenderreference+"/"+mesagepushid,mesagetextbody);

        }
    }
}
