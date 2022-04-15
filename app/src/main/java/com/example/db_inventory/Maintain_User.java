package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Maintain_User extends AppCompatActivity {

    Button btn_create, btn_access;
    DatabaseReference userRef, adminRef;
    RecyclerView recyclerView_user, recyclerView_admin;
    FirebaseRecyclerAdapter UserAdapter, AdminAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_user);

        btn_create=findViewById(R.id.btn_create_user);
        btn_access=findViewById(R.id.btn_access_right);

        //Connecting recycle view with data by using adapter
        recyclerView_user=findViewById(R.id.recycleView_User);
        LinearLayoutManager layoutManagerUser=new LinearLayoutManager(this);
        layoutManagerUser.setStackFromEnd(true);
        layoutManagerUser.setReverseLayout(true);
        recyclerView_user.setHasFixedSize(true);
        recyclerView_user.setLayoutManager(layoutManagerUser);

        recyclerView_admin=findViewById(R.id.recycleView_Admin);
        LinearLayoutManager layoutManagerAdmin=new LinearLayoutManager(this);
        layoutManagerAdmin.setStackFromEnd(true);
        layoutManagerAdmin.setReverseLayout(true);
        recyclerView_admin.setHasFixedSize(true);
        recyclerView_admin.setLayoutManager(layoutManagerAdmin);

        userRef= FirebaseDatabase.getInstance().getReference("Users").child("User");
        userRef.keepSynced(true);
        adminRef=FirebaseDatabase.getInstance().getReference("Users").child("Admin");
        adminRef.keepSynced(true);

        String users=getIntent().getStringExtra("Users");

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2_create_User=new Intent(getApplicationContext(), Create_User.class);
                intent2_create_User.putExtra("Users", users);

                startActivity(intent2_create_User);
            }
        });

        btn_access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2_create_User=new Intent(getApplicationContext(), Access_Right.class);
                intent2_create_User.putExtra("Users", users);
                startActivity(intent2_create_User);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        //User List Adapter
        FirebaseRecyclerOptions<User_List_class> userAdapter=new FirebaseRecyclerOptions.Builder<User_List_class>()
            .setQuery(userRef, User_List_class.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<User_List_class,UserViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<User_List_class, UserViewHolder>(userAdapter) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, final int position, @NonNull User_List_class model) {
                holder.Name.setText(model.getUsername());
                holder.Roles.setText(model.getRole());
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recycle_list,parent,false);
                return new UserViewHolder(view);
            }
        };

        recyclerView_user.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        //Admin List Adapter
        FirebaseRecyclerOptions<User_List_class> adminAdapter= new FirebaseRecyclerOptions.Builder<User_List_class>()
                .setQuery(adminRef,User_List_class.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<User_List_class,UserViewHolder> firebaseRecyclerAdapter1=new FirebaseRecyclerAdapter<User_List_class, UserViewHolder>(adminAdapter) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User_List_class model) {
                holder.Name.setText(model.getUsername());
                holder.Roles.setText(model.getRole());
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recycle_list,parent,false);
                return new UserViewHolder(view);
            }
        };
        recyclerView_admin.setAdapter(firebaseRecyclerAdapter1);
        firebaseRecyclerAdapter1.startListening();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mUserView;
        TextView Name;
        TextView Roles;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mUserView=itemView;
            Name=mUserView.findViewById(R.id.textView_Name_list);
            Roles=mUserView.findViewById(R.id.textView_role);
        }

        /*public void setUsername(String username){
            Name=mUserView.findViewById(R.id.textView_Name);
            Name.setText(username);
        }

        public void setRole(String roles){
            Roles=mUserView.findViewById(R.id.textView_role);
            Roles.setText(roles);
        }*/
    }

    public void onBackPressed() {
        super.onBackPressed();
        String users=getIntent().getStringExtra("Users");
        Intent intent2homepage = new Intent(getApplicationContext(), Home_Page.class);
        intent2homepage.putExtra("Users", users);
        startActivity(intent2homepage);
    }
}