package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private String visit_id;
    private TextView textViewprofilename, textViewprofilestatus;
    private CircleImageView circleImageView;
    private Button button;
    private DatabaseReference databaseReference, ChatrequestRef, ContactchatRequest;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String Current_State;
    private Button buttoncancel;
    private String curentuserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        ChatrequestRef = FirebaseDatabase.getInstance().getReference().child("chatrequest");
        ContactchatRequest = FirebaseDatabase.getInstance().getReference().child("contactrequest");
        curentuserid = firebaseAuth.getCurrentUser().getUid();
        visit_id = getIntent().getExtras().get("visit_id").toString();
        //  Toast.makeText(getApplicationContext(),"clicked number is ::"+visit_id,Toast.LENGTH_LONG).show();
        circleImageView = (CircleImageView) findViewById(R.id.profileprofile);
        textViewprofilename = (TextView) findViewById(R.id.usernameprofileA);
        textViewprofilestatus = (TextView) findViewById(R.id.visit_status);
        buttoncancel = (Button) findViewById(R.id.acceptrequest);
        button = (Button) findViewById(R.id.sendmessagebuuton);
        Current_State = "new";

    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.child("Users").child(visit_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("image")) {
                    String profileurl = dataSnapshot.child("image").getValue().toString();
                    String username = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    Picasso.get().load(profileurl).centerCrop().fit().into(circleImageView);
                    textViewprofilename.setText(username);
                    textViewprofilestatus.setText(status);
                    managechatrequest();
                } else {
                    String username = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    textViewprofilename.setText(username);
                    textViewprofilestatus.setText(status);
                    managechatrequest();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void managechatrequest() {
        ChatrequestRef.child(curentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(visit_id)) {
                        String requestmessage = dataSnapshot.child(visit_id).child("request_type").getValue().toString();
                        if (requestmessage.equals("sent")) {
                            Current_State = "request_sent";
                            button.setText("Cancel Chat Request");
                        } else if (requestmessage.equals("recive")) {
                            Current_State = "request recived";
                            button.setText("Accept the chat request");
                            buttoncancel.setVisibility(View.VISIBLE);
                            // buttoncancel.setText("Accept the request");
                            buttoncancel.setEnabled(true);
                            buttoncancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cancelchatrequest();
                                    buttoncancel.setEnabled(false);
                                    buttoncancel.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }
                    else {
                        ContactchatRequest.child(curentuserid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(visit_id)){
                                    Current_State="friend";
                                    button.setText("Remove this contact");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (!visit_id.equals(curentuserid)) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    button.setEnabled(false);
                    if (Current_State.equals("new")) {
                        sendChatRequest();
                    }
                    if (Current_State.equals("request_sent")) {
                        cancelchatrequest();
                    }
                    if (Current_State.equals("request recived")) {
                        AcceptChatRequest();
                    }
                    if (Current_State.equals("friend")){
                        removeContact();
                    }
                }
            });
        } else {
            button.setVisibility(View.INVISIBLE);
        }
    }

    public void sendChatRequest() {
        ChatrequestRef.child(curentuserid).child(visit_id).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ChatrequestRef.child(visit_id).child(curentuserid).child("request_type").setValue("recive").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Current_State = "request_sent";
                                        button.setEnabled(true);
                                        button.setText("cancel chat request");
                                    }
                                }
                            });
                        }
                    }
                });
    }

    public void cancelchatrequest() {
        ChatrequestRef.child(curentuserid).child(visit_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ChatrequestRef.child(visit_id).child(curentuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Current_State = "new";
                                button.setEnabled(true);
                                button.setText("send message");
                            }
                        }
                    });
                }
            }
        });
    }

    public void AcceptChatRequest() {
        ContactchatRequest.child(curentuserid).child(visit_id).child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ContactchatRequest.child(visit_id).child(curentuserid).child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ChatrequestRef.child(curentuserid).child(visit_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            ChatrequestRef.child(visit_id).child(curentuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Current_State = "friend";
                                                        button.setEnabled(true);
                                                        button.setText("Remove this contact");
                                                        buttoncancel.setVisibility(View.INVISIBLE);
                                                        buttoncancel.setEnabled(false);
                                                    }
                                                }
                                            });

                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
public void removeContact(){
    ContactchatRequest.child(curentuserid).child(visit_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                ContactchatRequest.child(visit_id).child(curentuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Current_State = "new";
                            button.setEnabled(true);
                            button.setText("send message");
                        }
                    }
                });
            }
        }
    });
}
}
