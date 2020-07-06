package com.example.whatsapp;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private RecyclerView recyclerViewchat;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceUser;
    private String cureentUserid;
    private String profileimage = "default_image";

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_chat, container, false);
        recyclerViewchat = (RecyclerView) view.findViewById(R.id.recyclechatlist);
        recyclerViewchat.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewchat.hasFixedSize();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("contactrequest");
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        cureentUserid = firebaseAuth.getCurrentUser().getUid();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(databaseReference, Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts, ChatViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder chatViewHolder, final int i, @NonNull Contacts contacts) {
                final String USerid = getRef(i).getKey();
                databaseReferenceUser.child(USerid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("image")) {
                                profileimage = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(profileimage).into(chatViewHolder.imageViewprofile);
                            }
                            final String name = dataSnapshot.child("name").getValue().toString();
                            String ststus = dataSnapshot.child("status").getValue().toString();
                            chatViewHolder.textViewname.setText(name);
                            chatViewHolder.textViewstatus.setText("Last seen" + "date" + "time");
                            chatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), ChatActiivty.class);
                                    intent.putExtra("username", name);
                                    intent.putExtra("visit_id", USerid);
                                    intent.putExtra("profileurl", profileimage);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
                return new ChatViewHolder(view);
            }
        };
        recyclerViewchat.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textViewname, textViewstatus;
        CircleImageView imageViewprofile;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewname = itemView.findViewById(R.id.friendname);
            textViewstatus = itemView.findViewById(R.id.frienstatus);
            imageViewprofile = itemView.findViewById(R.id.circlefriend);
        }
    }
}
