package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FindFriendActivity extends AppCompatActivity {
private Toolbar toolbar;
private FirebaseAuth firebaseAuth;
private FirebaseUser firebaseUser;
private String currentUserid;
private DatabaseReference databaseReference;
private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        currentUserid=firebaseAuth.getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        toolbar=(Toolbar)findViewById(R.id.findfriendinclude);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FindFriends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView=(RecyclerView)findViewById(R.id.recyclevirefriend);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> firebaseRecyclerOptions=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(databaseReference,Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts,RecycleViewHiolder> adapter=new FirebaseRecyclerAdapter<Contacts,
                RecycleViewHiolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull RecycleViewHiolder holder, final int position, @NonNull Contacts model) {
holder.textViewname.setText(model.getName());
holder.textViewstatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.imageViewprofile);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
String visit_id=getRef(position).getKey();
                        Intent intent=new Intent(FindFriendActivity.this,ProfileActivity.class);
                        intent.putExtra("visit_id",visit_id);
                        startActivity(intent);
                    }
                });


            }

            @NonNull
            @Override
            public RecycleViewHiolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
              View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
              return new RecycleViewHiolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        //adapter.startListening();*/
    }
    public static class RecycleViewHiolder extends RecyclerView.ViewHolder {
        TextView textViewname, textViewstatus;
        CircleImageView imageViewprofile;

        public RecycleViewHiolder(@NonNull View itemView) {
            super(itemView);
            textViewname = itemView.findViewById(R.id.friendname);
            textViewstatus = itemView.findViewById(R.id.frienstatus);
            imageViewprofile = itemView.findViewById(R.id.circlefriend);
            //}
        }
    }
}
