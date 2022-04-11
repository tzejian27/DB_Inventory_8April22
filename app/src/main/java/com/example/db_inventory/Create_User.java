package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Create_User extends AppCompatActivity {

    Button btn_create, btn_cancel;
    EditText et_username, et_pass1, et_pass2;
    Spinner role;
    DatabaseReference adminRef, usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        setTitle("Create User");

        et_username = findViewById(R.id.username_create_user);
        et_pass1 = findViewById(R.id.pass_create_user);
        et_pass2 = findViewById(R.id.pass_confirm_cu);
        role = findViewById(R.id.role_cu);

        btn_create = findViewById(R.id.btn_register_cu);
        btn_cancel = findViewById(R.id.btn_cancel_cu);

        usersRef = FirebaseDatabase.getInstance().getReference("Users").child("User");
        usersRef.keepSynced(true);

        adminRef = FirebaseDatabase.getInstance().getReference("Users").child("Admin");
        adminRef.keepSynced(true);

        String[] roles = {"user", "admin"};

        // Creating an Array Adapter to populate the spinner with the data in the string resources
        ArrayAdapter<String> addRole = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, roles);
        // Specify the layout to use when the list of choices appears
        addRole.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        role.setAdapter(addRole);

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_username.getText().toString().trim();
                String pass1 = et_pass1.getText().toString().trim();
                String pass2 = et_pass2.getText().toString().trim();
                String roleSP = role.getSelectedItem().toString().trim();

                //Set ref so username will be parent to the data
                String nameRef = username + "/";

                if (username.isEmpty()) {
                    et_username.setError("Username is required");
                    et_username.requestFocus();
                    return;
                }

                if (pass1.isEmpty()) {
                    et_pass1.setError("Password is required");
                    et_pass1.requestFocus();
                    return;
                }

                if (pass2.isEmpty()) {
                    et_pass2.setError("Confirm Password is required");
                    et_pass2.requestFocus();
                    return;
                }

                if (!pass1.equals(pass2)) {
                    Toast.makeText(getApplicationContext(), "Password not match", Toast.LENGTH_SHORT).show();
                    et_pass1.setError("Password not match");
                    et_pass1.requestFocus();
                } else {
                    if (roleSP.equals("admin")) {
                        adminRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.child(username).exists()) {
                                    Map dataMap1 = new HashMap();
                                    dataMap1.put("Username", username);
                                    dataMap1.put("Password", pass1);
                                    dataMap1.put("Role", roleSP);

                                    Map dataMap2 = new HashMap();
                                    dataMap2.put(nameRef + "/", dataMap1);
                                    adminRef.updateChildren(dataMap2);

                                    //Set text as empty after success
                                    et_username.setText("");
                                    et_pass1.setText("");
                                    et_pass2.setText("");

                                    Intent intent2MaintainUser = new Intent(getApplicationContext(), Maintain_User.class);
                                    startActivity(intent2MaintainUser);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Account already exist", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else if (roleSP.equals("user")) {
                        usersRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.child(username).exists()) {
                                    Map dataMap1 = new HashMap();
                                    dataMap1.put("Username", username);
                                    dataMap1.put("Password", pass1);
                                    dataMap1.put("Role", roleSP);

                                    Map dataMap2 = new HashMap();
                                    dataMap2.put(nameRef + "/", dataMap1);
                                    usersRef.updateChildren(dataMap2);

                                    //Set text as empty after success
                                    et_username.setText("");
                                    et_pass1.setText("");
                                    et_pass2.setText("");

                                    Intent intent2MaintainUser = new Intent(getApplicationContext(), Maintain_User.class);
                                    startActivity(intent2MaintainUser);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Account already exist", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "Something wrong with role", Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2MaintainUser = new Intent(getApplicationContext(), Maintain_User.class);
                startActivity(intent2MaintainUser);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent2back = new Intent(getApplicationContext(), Maintain_User.class);
        startActivity(intent2back);
    }
}