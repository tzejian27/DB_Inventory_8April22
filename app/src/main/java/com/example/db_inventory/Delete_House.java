package com.example.db_inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Delete_House extends AppCompatActivity {

    Button b1,b2;
    TextView e1;
    DatabaseReference databaseReference;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_house);

        b1=(Button)findViewById(R.id.btn_House_Delete_Cancel);
        b2=(Button)findViewById(R.id.btn_House_Delete_Enter);

        Intent intent1 = getIntent();
        key = intent1.getStringExtra("Key");
        e1=findViewById(R.id.editText_House_Delete);
        final String name = intent1.getStringExtra("name");

        e1.setText(name);

        databaseReference= FirebaseDatabase.getInstance().getReference("House").child(key);
        databaseReference.keepSynced(true);

        String users=getIntent().getStringExtra("Users");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Delete_House.this,House_List.class);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(name).removeValue();
                //   @Override
                //  public void onComplete(@NonNull Task<Void> task) {
                //     if (task.isSuccessful()) {
                Toast.makeText(Delete_House.this, "Delete Successful ! !", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Delete_House.this,MainActivity.class);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();


            }
        });
    }
}