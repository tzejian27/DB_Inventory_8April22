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
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        //THIS IS LOGIN PAGE FOR USER
        setTitle("LoginPage (User)");
        txtName = findViewById(R.id.name_login_user);
        txtPw = findViewById(R.id.pass_login_user);

        btnLogin = findViewById(R.id.btn_login_user);

        progressbar_main = findViewById(R.id.progressbar_main_user);

        // CREATING A NEW DBHANDLER CLASS
        // AND PASSING OUR CONTEXT TO IT.
        dbHandler = new DBHandler(getApplicationContext());

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

        LoginRef = FirebaseDatabase.getInstance().getReference().child("Users").child("User");

        //MATCH THE USERNAME AND GET THE PASSWORD FOR THE CURRENT USERNAME
        LoginRef.child(Username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //CHECK ISN'T THE USERNAME EXIST
                if (snapshot.exists()) {
                    String pwd = snapshot.child("Password").getValue().toString();

                    //MATCHING THE PASSWORD
                    if (Password.equals(pwd)) {
                        username = Username;
                        String users = "User";
                        //PASSING NEEDED DATA TO THE HOME PAGE
                        //ROLE WILL BE USED BY APPLICATION TO CHECK WHETHER USER ARE ALLOWED WITH THE FUNCTION
                        Intent intent = new Intent(getApplicationContext(), Home_Page.class);
                        intent.putExtra(Username, username);
                        intent.putExtra("Users", users);
                        intent.putExtra("Role", "user");

                        //SAVE USER INFO TO DB
                        String userName = txtName.getText().toString();
                        String userRole = users;
                        dbHandler.addUserInfo(userName, userRole);

                        startActivity(intent);
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
                        Toast.makeText(getApplicationContext(), "Password incorrect ...", Toast.LENGTH_SHORT).show();
                        progressbar_main.setVisibility(View.INVISIBLE);
                    }
                } else {
                    //GIVING "Username incorrect ..." FEEDBACK WHEN USERNAME DOESN'T EXIST
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