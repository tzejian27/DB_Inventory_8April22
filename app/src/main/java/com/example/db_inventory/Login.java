package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    public static String username;
    Button btnLogin;
    EditText txtName, txtPw;
    DatabaseReference LoginRef;
    private ProgressBar progressbar_main;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //THIS IS LOGIN PAGE FOR ADMIN
        setTitle("LoginPage (Admin)");
        txtName = findViewById(R.id.name_login);
        txtPw = findViewById(R.id.pass_login);

        btnLogin = findViewById(R.id.btn_login);

        progressbar_main = findViewById(R.id.progressbar_main);

        // CREATING A NEW DBHANDLER CLASS
        // AND PASSING OUR CONTEXT TO IT.
        dbHandler = new DBHandler(getApplicationContext());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Login();
            }
        });

        //IF ALREADY REGISTER, NO NEED TO LOGIN EVERY TIME
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Home_Page.class));
        }


    }

    private void Login() {
        String Username = txtName.getText().toString().trim();
        String Password = txtPw.getText().toString().trim();

        //VERIFY THE LOGIN INPUT PROVIDED
        if (Username.isEmpty()) {
            txtName.setError("Username is empty");
            txtName.requestFocus();
            return;
        }

        if (Password.isEmpty()) {
            txtPw.setError("Password is empty");
            txtPw.requestFocus();
            return;
        }

        //SHOW THE PROGRESS WHEN BUTTON IS CLICKED
        //WHERE USERS CAN VIEW THEIR LOGIN PROGRESS
        progressbar_main.setVisibility(View.VISIBLE);

        LoginRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Admin");
        LoginRef.child(Username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String pwd = snapshot.child("Password").getValue().toString();


                    if (Password.equals(pwd)) {
                        username = Username;
                        String users = "Admin";
                        Intent intent = new Intent(Login.this, Home_Page.class);

                        //INTENT USER'S ROLE AND NAME TO ANOTHER PAGE
                        intent.putExtra(Username, username);
                        intent.putExtra("Users", users);
                        intent.putExtra("Role", "admin");

                        String userName = txtName.getText().toString();
                        String userRole = users;

                        //SAVE USER INFO TO DB
                        //WILL BE USED TO CHECK USER ACCESS RIGHT
                        dbHandler.addUserInfo(userName, userRole);

                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        finish();
                        //EMPTY TEXT BOX WHEN LOGIN IS SUCCESS
                        txtName.setText("");
                        txtPw.setText("");

                        //CLOSE LOGIN PROGRESS BAR
                        //GIVING "LOGIN SUCCESSFULLY" FEEDBACK WHEN PASSWORD IS MATCH
                        progressbar_main.setVisibility(View.INVISIBLE);

                        Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                    } else {

                        //GIVING "Password incorrect ..." FEEDBACK WHEN PASSWORD IS NOT MATCH
                        Toast.makeText(Login.this, "Password incorrect ...", Toast.LENGTH_SHORT).show();
                        progressbar_main.setVisibility(View.INVISIBLE);
                    }
                } else {
                    //GIVING "Username incorrect ..." FEEDBACK WHEN USERNAME DOESN'T EXIST
                    Toast.makeText(Login.this, "Username incorrect ...", Toast.LENGTH_SHORT).show();
                    progressbar_main.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent2login = new Intent(getApplicationContext(), Login_Menu.class);
        startActivity(intent2login);
    }
}