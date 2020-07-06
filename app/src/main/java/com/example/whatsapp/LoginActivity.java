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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    Button loginButton,PhoneLoginButton;
    private FirebaseAuth firebaseAuth;
    EditText editTextmail,editTextpass;
    TextView textViewforgetPass,textViewneedAccount;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth=FirebaseAuth.getInstance();
        //intialized();
        loginButton=(Button)findViewById(R.id.button);
        progressDialog=new ProgressDialog(this);
        PhoneLoginButton=(Button)findViewById(R.id.PhoneButton);
        editTextmail=(EditText)findViewById(R.id.loginemail);
        editTextpass=(EditText)findViewById(R.id.loginPass);
        textViewforgetPass=(TextView)findViewById(R.id.forgetpass);
        textViewneedAccount=(TextView)findViewById(R.id.newAccount);
        textViewneedAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               registerActivity();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
currentUser=firebaseAuth.getCurrentUser();
PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(LoginActivity.this,PhoneLoginAuthentication.class)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
});
    }
/*public void intialized(){
        loginButton=(Button)findViewById(R.id.button);
    PhoneLoginButton=(Button)findViewById(R.id.PhoneButton);
    editTextmail=(EditText)findViewById(R.id.loginemail);
    editTextpass=(EditText)findViewById(R.id.loginPass);
    textViewforgetPass=(TextView)findViewById(R.id.forgetpass);
    textViewneedAccount=(TextView)findViewById(R.id.newAccount);
}*/

public void Login(){
    String mail=editTextmail.getText().toString().trim();
    String passw=editTextpass.getText().toString().trim();
    if (mail.isEmpty()){
        editTextmail.setError("Please enter your email");
        editTextmail.requestFocus();
        return;
    }
    if (passw.isEmpty()){
        editTextpass.setError("Please enter your pass");
        editTextpass.requestFocus();
        return;
    }
    progressDialog.setMessage("Please waiting while Log in  Your Account");
    progressDialog.setTitle("Login your Account");
    progressDialog.setCanceledOnTouchOutside(true);
    progressDialog.show();
    firebaseAuth.signInWithEmailAndPassword(mail,passw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()){
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"LoginSuccesful",Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this,SettingActivity.class));
                finish();
            }
        }
    }).addOnCanceledListener(new OnCanceledListener() {
        @Override
        public void onCanceled() {
            Toast.makeText(getApplicationContext(),"Login failed",Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    });
}
public void registerActivity(){
    startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
}
   /* @Override
    protected void onStart() {
        super.onStart();
        if (currentUser!=null)
        {
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
    }*/
}
