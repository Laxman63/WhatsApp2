package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
;import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editTextmessage;
    private ImageView imageView;
    private ScrollView scrollView;
    private TextView textView;
    private String groupname, currentusername, cueentDate, message, CureentTime;
    private String currentuserid, messageKey;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userseReference, groupreference, groupMesagekeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        groupname = getIntent().getStringExtra("groupname").toString();
        userseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        groupreference = FirebaseDatabase.getInstance().getReference().child("Group").child(groupname);
        groupMesagekeyRef = FirebaseDatabase.getInstance().getReference();
        toolbar = (Toolbar) findViewById(R.id.groupchatname);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(groupname);
        firebaseAuth = FirebaseAuth.getInstance();
        currentuserid = firebaseAuth.getCurrentUser().getUid();
        editTextmessage = (EditText) findViewById(R.id.chatwrite);
        imageView = (ImageView) findViewById(R.id.sendbutton);
        textView = (TextView) findViewById(R.id.textchat);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        userseReference.child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentusername = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM dd, yyyy");
        cueentDate = simpleDateFormat.format(calendar.getTime());
        Calendar calenTime = Calendar.getInstance();
        SimpleDateFormat simpletimeFormatet = new SimpleDateFormat("hh:mm a");
        CureentTime = simpletimeFormatet.format(calenTime.getTime());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = editTextmessage.getText().toString();
                if (message.isEmpty()) {
                    editTextmessage.setError("empty message can be send");
                    editTextmessage.requestFocus();
                    return;
                }
                messageKey = groupreference.push().getKey();
                HashMap<String, Object> hashMap = new HashMap<>();
                groupreference.updateChildren(hashMap);
                groupMesagekeyRef = groupreference.child(messageKey);
                HashMap<String, Object> hashMapmessageinfo = new HashMap<>();
                hashMapmessageinfo.put("name", currentusername);
                hashMapmessageinfo.put("message", message);
                hashMapmessageinfo.put("date", cueentDate);
                hashMapmessageinfo.put("time", CureentTime);
                groupMesagekeyRef.updateChildren(hashMapmessageinfo);

                editTextmessage.setText("");
                //scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        groupreference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    displayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    displayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayMessage(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()) {
            String date = (String) ((DataSnapshot) iterator.next()).getValue();
            String message = (String) ((DataSnapshot) iterator.next()).getValue();
            String CHATSender = (String) ((DataSnapshot) iterator.next()).getValue();
            String time = (String) ((DataSnapshot) iterator.next()).getValue();
            textView.append(CHATSender + "\n" + message + "\n" + date + "     " + time + "\n\n\n\n\n\n");
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);}
    }
}
