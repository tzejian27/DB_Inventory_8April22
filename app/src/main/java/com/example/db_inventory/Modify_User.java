package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Modify_User extends AppCompatActivity {

    Button btn_confirm, btn_cancel;
    EditText et_username, et_c_password, et_pass1, et_pass2;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);

        et_username = findViewById(R.id.username_modify_user);
        et_c_password = findViewById(R.id.current_pass);
        et_pass1 = findViewById(R.id.pass_modify_user);
        et_pass2 = findViewById(R.id.pass_confirm_modify);

        btn_confirm = findViewById(R.id.btn_confirm_mu);
        btn_cancel = findViewById(R.id.btn_cancel_mu);

        usersRef = FirebaseDatabase.getInstance().getReference("Users").child("User");
        usersRef.keepSynced(true);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("Name");
        final String password = intent.getStringExtra("Password");
        String users = getIntent().getStringExtra("Users");

        setTitle("eStock_Modify User-" + name);

        et_username.setText(name);
        et_c_password.setText(password);
        et_username.setEnabled(false);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_username.getText().toString().trim().replace("/", "|");
                String current_password = et_c_password.getText().toString().trim();
                String pass1 = et_pass1.getText().toString().trim();
                String pass2 = et_pass2.getText().toString().trim();


                if (username.isEmpty()) {
                    et_username.setError("Username is required");
                    et_username.requestFocus();
                    return;
                }

                if (current_password.isEmpty()) {
                    et_c_password.setError("Current Password is required");
                    et_c_password.requestFocus();
                    return;
                }

                if (pass1.isEmpty()) {
                    et_pass1.setError("New Password is required");
                    et_pass1.requestFocus();
                    return;
                }

                if (pass2.isEmpty()) {
                    et_pass2.setError("Confirm Password is required");
                    et_pass2.requestFocus();
                    return;
                }

                if (pass1.equals(pass2)) {
                    usersRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Map dataMap1 = new HashMap();
                                dataMap1.put("Username", username);
                                dataMap1.put("Password", pass2);

                                usersRef.child(username).updateChildren(dataMap1);

                                et_pass1.setText("");
                                et_pass2.setText("");

                                Intent intent1 = new Intent(getApplicationContext(), Maintain_User.class);
                                intent1.putExtra("Users", users);
                                startActivity(intent1);
                            } else {
                                Toast.makeText(getApplicationContext(), "User not exist", Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String users = getIntent().getStringExtra("Users");
                Intent intent = new Intent(getApplicationContext(), Maintain_User.class);
                intent.putExtra("Users", users);
                startActivity(intent);
            }
        });

    }

    public void onBackPressed() {
        super.onBackPressed();
        String users = getIntent().getStringExtra("Users");
        Intent intent = new Intent(getApplicationContext(), Maintain_User.class);
        intent.putExtra("Users", users);
        startActivity(intent);
    }
}