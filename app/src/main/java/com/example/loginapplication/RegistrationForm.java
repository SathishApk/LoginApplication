package com.example.loginapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationForm extends AppCompatActivity {
private EditText txtEmail, txtPassword, txtFirstName, txtLastName;
private String userId;
private Button btnRegister;
private FirebaseAuth fAuth;
private FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);
        txtEmail = findViewById(R.id.txtEmail_Id);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtPassword = findViewById(R.id.txtPasscode);
        btnRegister = findViewById(R.id.btnRegister);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = txtEmail.getText().toString();
                final String firstName = txtFirstName.getText().toString();
                final String lastName = txtLastName.getText().toString();
                String password = txtPassword.getText().toString();
                if(!(firstName.isEmpty())){
                    if(!lastName.isEmpty()){
                        if(!email.isEmpty()){
                            if(!password.isEmpty()){
                                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                        {
                                            FirebaseUser fUser = fAuth.getCurrentUser();
                                            fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(RegistrationForm.this, "Email Sent for Verification", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(RegistrationForm.this, "Email sending Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            userId = fAuth.getCurrentUser().getUid();
                                            DocumentReference docref = fStore.collection("users").document(userId);
                                            Map<String,Object> user = new HashMap<>();
                                            user.put("Email",email);
                                            user.put("FirstName",firstName);
                                            user.put("LastName", lastName);
                                            docref.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(RegistrationForm.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(RegistrationForm.this, "User Creation Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            startActivity(new Intent(getApplicationContext(), WelcomePage.class));
                                        }
                                        else
                                        {
                                            Toast.makeText(RegistrationForm.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                        else{
                            txtFirstName.setError("Enter valid Email");
                        }
                    }
                    else{
                        txtFirstName.setError("Enter LastName");
                    }
                }
                else{
                    txtFirstName.setError("Enter FirstName");
                }
            }
        });
    }
}