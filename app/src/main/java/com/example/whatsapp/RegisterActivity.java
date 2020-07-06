package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
private Button registerButton;
private EditText editTextemail,editTextpass;
TextView textViewalreadyAccount;
private FirebaseAuth firebaseAuth;
private DatabaseReference databaseReference;
private ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        databaseReference=FirebaseDatabase.getInstance().getReference("authdata");
        progressBar=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();
        registerButton=(Button)findViewById(R.id.registerbutton);
        textViewalreadyAccount=(TextView)findViewById(R.id.alreadyaccount);
        editTextemail=(EditText)findViewById(R.id.registeremail);
        editTextpass=(EditText)findViewById(R.id.registerPass);
        textViewalreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alradyAccount();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }
    public void alradyAccount(){
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
    }
public void createAccount(){

        final String email=editTextemail.getText().toString().trim();
        String pass=editTextpass.getText().toString().trim();
        if (email.isEmpty()){
            editTextemail.setError("Please enter your email");
            editTextemail.requestFocus();
            return;
        }
    if (pass.isEmpty()){
        editTextpass.setError("Please enter your pass");
        editTextpass.requestFocus();
        return;
    }
    progressBar.setMessage("Please waiting while Creating Your Account");
    progressBar.setTitle("Creating Your Account");
    progressBar.setCanceledOnTouchOutside(true);
    progressBar.show();
    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()){
                String userid=firebaseAuth.getCurrentUser().getUid();
                databaseReference.child(userid).setValue(email);
                Toast.makeText(getApplicationContext(),"your account created",Toast.LENGTH_LONG).show();
                progressBar.dismiss();
                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
            }
        }
    }).addOnCanceledListener(new OnCanceledListener() {
        @Override
        public void onCanceled() {
Toast.makeText(getApplicationContext(),"account failed",Toast.LENGTH_LONG).show();
progressBar.dismiss();
        }
    });
}

}
