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


public class Login extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public String userCurrent = "";
    static int totalCounts = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context2 = this;
        int durationToast = Toast.LENGTH_SHORT;
        Toast toastFailLogin = Toast.makeText(context2, "Wrong username or password", durationToast);
        setContentView(R.layout.login);


        Button loginBtn = (Button) findViewById(R.id.login_button);
        Button signupBtn = (Button) findViewById(R.id.sign_button);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText userField = (EditText) findViewById(R.id.loginUser);
                EditText passField = (EditText) findViewById(R.id.loginPass);

                DatabaseReference ref1 = firebaseDatabase.getReference("Users");
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            CustomUser usr = snapshot1.getValue(CustomUser.class);

                            if ((usr.getUsername().equals(userField.getText().toString())) &&
                                    usr.getPassword().equals(passField.getText().toString())) {

                                userCurrent = userField.getText().toString();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("userPassed", userCurrent);
                                Login.this.startActivity(intent);
                                setContentView(R.layout.activity_main);

                            } else {
                                toastFailLogin.show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(Login.this, SignUp.class);
                Login.this.startActivity(intent2);
                setContentView(R.layout.signup);
            }
        });

    }

}
