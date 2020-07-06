package com.example.whatsapp;


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
public class ContactFragment extends Fragment {
private RecyclerView recyclerViewcotact;
private DatabaseReference contactreference,userreference;
private FirebaseAuth firebaseAuth;
private FirebaseUser firebaseUser;
private String currentuserid;
    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
   View view=inflater.inflate(R.layout.fragment_contact,container,false);
   recyclerViewcotact=(RecyclerView)view.findViewById(R.id.contact_recycleview);
   recyclerViewcotact.setLayoutManager(new LinearLayoutManager(getContext()));
   recyclerViewcotact.hasFixedSize();
   contactreference= FirebaseDatabase.getInstance().getReference().child("contactrequest");
   userreference=FirebaseDatabase.getInstance().getReference().child("Users");
   firebaseAuth=FirebaseAuth.getInstance();
   currentuserid=firebaseAuth.getCurrentUser().getUid();

   return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions firebaseRecyclerOptions=new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(contactreference.child(currentuserid),Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder contactsViewHolder, final int i, @NonNull Contacts contacts) {
                String clickid=getRef(i).getKey();
                userreference.child(clickid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
      if (dataSnapshot.hasChild("image")){
       String image=dataSnapshot.child("image").getValue().toString();
       String name=dataSnapshot.child("name").getValue().toString();
       String status=dataSnapshot.child("status").getValue().toString();
       contactsViewHolder.textViewname.setText(name);
       contactsViewHolder.textViewstatus.setText(status);
          Picasso.get().load(image).into(contactsViewHolder.imageViewprofile);
}
          else {
    String name=dataSnapshot.child("name").getValue().toString();
    String status=dataSnapshot.child("status").getValue().toString();
    contactsViewHolder.textViewname.setText(name);
    contactsViewHolder.textViewstatus.setText(status);

          }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                /*contactsViewHolder.textViewname.setText(contacts.getName());
                contactsViewHolder.textViewstatus.setText(contacts.getStatus());
                Picasso.get().load(contacts.getImage()).placeholder(R.drawable.profile_image).into(contactsViewHolder.imageViewprofile);*/

            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout,parent,false);
               return new ContactsViewHolder(view);
            }
        };
        recyclerViewcotact.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
    public static class ContactsViewHolder extends RecyclerView.ViewHolder{
        TextView textViewname, textViewstatus;
        CircleImageView imageViewprofile;
        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewname = itemView.findViewById(R.id.friendname);
            textViewstatus = itemView.findViewById(R.id.frienstatus);
            imageViewprofile = itemView.findViewById(R.id.circlefriend);
        }
    }
}
