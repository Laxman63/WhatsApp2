package com.example.whatsapp;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private FirebaseAuth firebaseAuth;
    private TabLayout tabLayout;
    private TabAcessAdapter tabAcessAdapter;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Whatsapp");
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabAcessAdapter = new TabAcessAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAcessAdapter);
        tabLayout.setupWithViewPager(viewPager);
        currentUser = firebaseAuth.getCurrentUser();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            sendToLoginActivity();
            finish();
        }
//verifyUserExistance();
    }

    public void verifyUserExistance() {
        String currentUserid = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Users").child(currentUserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child("name").exists()) {
                    Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(MainActivity.this, SettingActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendToLoginActivity() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuoption, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.findFriend:
                startActivity(new Intent(MainActivity.this,FindFriendActivity.class));
                return true;
            case R.id.group:
                RequesttGroup();
                return true;
            case R.id.setting:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                return true;
            case R.id.logout:
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private void RequesttGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        builder.setTitle("Enter Group Nmae");
        final EditText editTextgroupname = new EditText(this);
        editTextgroupname.setHint("Modi fan");
        builder.setView(editTextgroupname);
        builder.setPositiveButton("create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = editTextgroupname.getText().toString();
                if (groupName.isEmpty()) {
                    editTextgroupname.setError("please enter your group name");
                    editTextgroupname.requestFocus();
                    return;
                }
                createGroup(groupName);
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        builder.create();
        builder.show();
    }

    private void createGroup(final String groupName) {
        databaseReference.child("Group").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), groupName + "is created", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}
