package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginAuthentication extends AppCompatActivity {
    private Button buttongetcode,buttonverify;
    private EditText editTextcode,editTextphone;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String codesent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login_authentication);
        firebaseAuth=FirebaseAuth.getInstance();
        buttongetcode=(Button)findViewById(R.id.getcode);
        buttonverify=(Button)findViewById(R.id.verifiy);
        editTextcode=(EditText)findViewById(R.id.verificationcode);
        editTextphone=(EditText)findViewById(R.id.phonenumber);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("loging... with Phone");
        progressDialog.setMessage("your Phone number is loging Please wait ");
        progressDialog.setIcon(R.drawable.ic_smartphone_black_24dp);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.create();
        buttongetcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttongetcode.setVisibility(View.INVISIBLE);
                buttonverify.setVisibility(View.VISIBLE);
                editTextcode.setVisibility(View.VISIBLE);
phonegetcode();
            }
        });
        buttonverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codenter=editTextcode.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codesent,codenter);
                signInWithPhoneAuthCredential(credential);
            }
        });
    }
    public void phonegetcode(){
        String phoneNumber=editTextphone.getText().toString();
       PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91"+phoneNumber,        // Phone number to verify
        60,                 // Timeout duration
            TimeUnit.SECONDS,   // Unit of timeout
        this,               // Activity (for callback binding)
            mCallbacks);  }
            PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
signInWithPhoneAuthCredential(phoneAuthCredential);
                    Toast.makeText(getApplicationContext(),"verification completed",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
Toast.makeText(getApplicationContext(),"verification failed",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codesent=s;

                }
            };
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.show();
startActivity(new Intent(PhoneLoginAuthentication.this,MainActivity.class));
                        finish();}
                    }
                });
    }


}
