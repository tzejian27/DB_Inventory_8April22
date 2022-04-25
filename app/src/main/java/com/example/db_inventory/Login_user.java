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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Login_user extends AppCompatActivity {

    public static String username;
    Button btnLogin;
    EditText txtName, txtPw;
    DatabaseReference LoginRef;
    private ProgressBar progressbar_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        //This is login page for user
        setTitle("LoginPage (User)");
        txtName = findViewById(R.id.name_login_user);
        txtPw = findViewById(R.id.pass_login_user);

        btnLogin = findViewById(R.id.btn_login_user);

        progressbar_main = findViewById(R.id.progressbar_main_user);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User_Login();
            }
        });
    }

    public void User_Login() {
        String Username = txtName.getText().toString().trim();
        String Password = txtPw.getText().toString().trim();

        //verify the login input provided
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

        //show the progress when button is clicked
        //where users can view their login progress
        progressbar_main.setVisibility(View.VISIBLE);

        LoginRef = FirebaseDatabase.getInstance().getReference().child("Users").child("User");

        //match the username and get the password for the current username
        LoginRef.child(Username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check isn't the username exist
                if (snapshot.exists()) {
                    String pwd = snapshot.child("Password").getValue().toString();

                    //matching the password
                    if (Password.equals(pwd)) {
                        username = Username;
                        String users = "User";
                        //passing needed data to the home page
                        //role will be used by application to check whether user are allowed with the function
                        Intent intent = new Intent(getApplicationContext(), Home_Page.class);
                        intent.putExtra(Username, username);
                        intent.putExtra("Users", users);
                        intent.putExtra("Role", "user");
                        startActivity(intent);
                        finish();
                        txtName.setText("");
                        txtPw.setText("");
                        progressbar_main.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Password incorrect ...", Toast.LENGTH_SHORT).show();
                        progressbar_main.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Username incorrect ...", Toast.LENGTH_SHORT).show();
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