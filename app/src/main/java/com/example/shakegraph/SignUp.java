package com.example.shakegraph;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignUp extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference ref = firebaseDatabase.getReference("Users");
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        Button buttonSave = (Button) findViewById(R.id.button_save);
        Button buttonBack = (Button) findViewById(R.id.button_back);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long value = snapshot.getChildrenCount();
                        Intent intent = new Intent(SignUp.this, Login.class);
                        SignUp.this.startActivity(intent);
                        addNewUser(value + 1);
                        setContentView(R.layout.login);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, Login.class);
                SignUp.this.startActivity(intent);
                setContentView(R.layout.login);
            }
        });
    }

    public void addNewUser(long user_id_number) {
        DatabaseReference ref1 = firebaseDatabase.getReference("Users").child(String.valueOf(user_id_number)).child("email");
        DatabaseReference ref2 = firebaseDatabase.getReference("Users").child(String.valueOf(user_id_number)).child("username");
        DatabaseReference ref3 = firebaseDatabase.getReference("Users").child(String.valueOf(user_id_number)).child("password");

        Context context = this;
        int durationToast = Toast.LENGTH_SHORT;
        Toast toastSuccess = Toast.makeText(context, "Success!", durationToast);
        Toast toastFail = Toast.makeText(context, "Passwords do not match", durationToast);
        Toast toastCredentials = Toast.makeText(context, "Username sau Email existent", durationToast);
        Toast emailFormat = Toast.makeText(context, "Wrong email format", durationToast);

        EditText mail = (EditText) findViewById(R.id.mailSign);
        EditText user = (EditText) findViewById(R.id.usernameSign);
        EditText pass = (EditText) findViewById(R.id.passwordSign);
        EditText confirmedPass = (EditText) findViewById(R.id.confirmPassSign);

        CustomUser newUser = new CustomUser(mail.getText().toString(), user.getText().toString(), pass.getText().toString());

        DatabaseReference refVerif = firebaseDatabase.getReference("Users");
        refVerif.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    CustomUser userVerif = snapshot1.getValue(CustomUser.class);
                    if (mail.getText().toString().trim().matches(emailPattern)) {
                        if (userVerif.getEmail().equals(mail.getText().toString()) ||
                                (userVerif.getUsername().equals(user.getText().toString()))) {
                            toastCredentials.show();
                            break;
                        } else {
                            if (pass.getText().toString().equals(confirmedPass.getText().toString())) {

                                ref1.setValue(newUser.getEmail());
                                ref2.setValue(newUser.getUsername());
                                ref3.setValue(newUser.getPassword());
                                toastSuccess.show();
                            } else {
                                toastFail.show();
                            }
                        }
                    } else {
                        emailFormat.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
