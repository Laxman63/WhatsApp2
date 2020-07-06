package com.example.whatsapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference chatreferencerequest, Userreference, Contactreference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String currentuserid;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_request, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerequest);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.hasFixedSize();
        chatreferencerequest = FirebaseDatabase.getInstance().getReference().child("chatrequest");
        firebaseAuth = FirebaseAuth.getInstance();
        Userreference = FirebaseDatabase.getInstance().getReference().child("Users");
        Contactreference = FirebaseDatabase.getInstance().getReference().child("contactrequest");
        currentuserid = firebaseAuth.getCurrentUser().getUid();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatreferencerequest.child(currentuserid), Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts, RequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder requestViewHolder, int i, @NonNull Contacts contacts) {
                requestViewHolder.itemView.findViewById(R.id.accept).setVisibility(View.VISIBLE);
                requestViewHolder.itemView.findViewById(R.id.cancel).setVisibility(View.VISIBLE);
                final String list_user_id = getRef(i).getKey();
                DatabaseReference getReference = getRef(i).child("request_type").getRef();
                getReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String type = dataSnapshot.getValue().toString();
                            if (type.equals("recive")) {
                                Userreference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("image")) {
                                            String name = dataSnapshot.child("name").getValue().toString();
                                            String ststus = dataSnapshot.child("status").getValue().toString();
                                            String profileimage = dataSnapshot.child("image").getValue().toString();
                                            requestViewHolder.textViewname.setText(name);
                                            requestViewHolder.textViewstatus.setText(ststus);
                                            Picasso.get().load(profileimage).into(requestViewHolder.imageViewprofile);
                                        }

                                        final String name = dataSnapshot.child("name").getValue().toString();
                                        String ststus = dataSnapshot.child("status").getValue().toString();
                                        requestViewHolder.textViewname.setText(name);
                                        requestViewHolder.textViewstatus.setText(ststus);

                                        requestViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence charSequence[] = new CharSequence[]{
                                                        "Accept", "Cancel"
                                                };
                                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                                                alertDialog.setTitle(name + "Chat Request");
                                                alertDialog.setItems(charSequence, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (which == 0) {
                                                            Contactreference.child(currentuserid).child(list_user_id).child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Contactreference.child(list_user_id).child(currentuserid).child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    chatreferencerequest.child(currentuserid).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                chatreferencerequest.child(list_user_id).child(currentuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        Toast.makeText(getContext(), "contact saved", Toast.LENGTH_LONG).show();
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
                                                        if (which == 1) {


                                                            chatreferencerequest.child(currentuserid).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        chatreferencerequest.child(list_user_id).child(currentuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                Toast.makeText(getContext(), "contact saved", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });


                                                        }
                                                    }
                                                });
                                                alertDialog.show();
                                            }
                                        });

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
            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
                return new RequestViewHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView textViewname, textViewstatus;
        CircleImageView imageViewprofile;
        Button buttonaccept, buttoncancel;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewname = itemView.findViewById(R.id.friendname);
            textViewstatus = itemView.findViewById(R.id.frienstatus);
            imageViewprofile = itemView.findViewById(R.id.circlefriend);
            buttonaccept = itemView.findViewById(R.id.accept);
            buttoncancel = itemView.findViewById(R.id.cancel);
        }
    }
}
