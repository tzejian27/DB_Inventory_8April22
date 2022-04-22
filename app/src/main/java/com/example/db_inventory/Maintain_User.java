package com.example.db_inventory;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Maintain_User extends AppCompatActivity {

    Button btn_create, btn_access;
    DatabaseReference userRef, adminRef;
    RecyclerView recyclerView_user, recyclerView_admin;
    FirebaseRecyclerAdapter UserAdapter, AdminAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_user);

        btn_create = findViewById(R.id.btn_create_user);
        btn_access = findViewById(R.id.btn_access_right);

        //Connecting recycle view with data by using adapter
        recyclerView_user = findViewById(R.id.recycleView_User);
        LinearLayoutManager layoutManagerUser = new LinearLayoutManager(this);
        layoutManagerUser.setStackFromEnd(true);
        layoutManagerUser.setReverseLayout(true);
        recyclerView_user.setHasFixedSize(true);
        recyclerView_user.setLayoutManager(layoutManagerUser);

        recyclerView_admin = findViewById(R.id.recycleView_Admin);
        LinearLayoutManager layoutManagerAdmin = new LinearLayoutManager(this);
        layoutManagerAdmin.setStackFromEnd(true);
        layoutManagerAdmin.setReverseLayout(true);
        recyclerView_admin.setHasFixedSize(true);
        recyclerView_admin.setLayoutManager(layoutManagerAdmin);

        userRef = FirebaseDatabase.getInstance().getReference("Users").child("User");
        userRef.keepSynced(true);
        adminRef = FirebaseDatabase.getInstance().getReference("Users").child("Admin");
        adminRef.keepSynced(true);

        String users = getIntent().getStringExtra("Users");

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2_create_User = new Intent(getApplicationContext(), Create_User.class);
                intent2_create_User.putExtra("Users", users);

                startActivity(intent2_create_User);
            }
        });

        btn_access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2_create_User = new Intent(getApplicationContext(), Access_Right.class);
                intent2_create_User.putExtra("Users", users);
                startActivity(intent2_create_User);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        String users = getIntent().getStringExtra("Users");
        //User List Adapter
        FirebaseRecyclerOptions<User_List_class> userAdapter = new FirebaseRecyclerOptions.Builder<User_List_class>()
                .setQuery(userRef, User_List_class.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<User_List_class, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User_List_class, UserViewHolder>(userAdapter) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull User_List_class model) {
                holder.Name.setText(model.getUsername());
                holder.Roles.setText(model.getRole());

                holder.mUserView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String key = model.getUsername();
                        final String role = model.getRole();
                        final String password = model.getPassword();
                        userRef = FirebaseDatabase.getInstance().getReference(key);
                        userRef.keepSynced(true);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                CharSequence[] option1 = new CharSequence[]{
                                        "Delete", "Modify"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(Maintain_User.this);
                                builder.setTitle("Select Option");
                                builder.setItems(option1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position) {
                                        if (position == 0) {
                                            CharSequence[] option2 = new CharSequence[]{
                                                    "Delete", "Cancel"
                                            };
                                            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(Maintain_User.this);
                                            deleteBuilder.setTitle("Are you confirm to delete " + key + " ?");
                                            deleteBuilder.setItems(option2, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    if (i == 0) {
                                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("User");
                                                        reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(Maintain_User.this, "Delete successfully", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(Maintain_User.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                    if (i == 1) {
                                                        dialogInterface.cancel();
                                                    }
                                                }
                                            });
                                            deleteBuilder.show();
                                        }

                                        if (position == 1) {

                                            Toast.makeText(Maintain_User.this, "Modify " + key, Toast.LENGTH_SHORT).show();
                                            //Modify user data
                                            Intent intent = new Intent(Maintain_User.this, Modify_User.class);
                                            intent.putExtra("Name", key);
                                            intent.putExtra("Role", role);
                                            intent.putExtra("Password", password);
                                            intent.putExtra("Users", users);
                                            Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                                            startActivity(intent);

                                        }
                                    }
                                });
                                builder.show();


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recycle_list, parent, false);
                return new UserViewHolder(view);
            }
        };

        recyclerView_user.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        //Admin List Adapter
        FirebaseRecyclerOptions<User_List_class> adminAdapter = new FirebaseRecyclerOptions.Builder<User_List_class>()
                .setQuery(adminRef, User_List_class.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<User_List_class, UserViewHolder> firebaseRecyclerAdapter1 = new FirebaseRecyclerAdapter<User_List_class, UserViewHolder>(adminAdapter) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User_List_class model) {
                holder.Name.setText(model.getUsername());
                holder.Roles.setText(model.getRole());
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recycle_list, parent, false);
                return new UserViewHolder(view);
            }
        };
        recyclerView_admin.setAdapter(firebaseRecyclerAdapter1);
        firebaseRecyclerAdapter1.startListening();
    }

    public void onBackPressed() {
        super.onBackPressed();
        String users = getIntent().getStringExtra("Users");
        Intent intent2homepage = new Intent(getApplicationContext(), Maintain.class);
        intent2homepage.putExtra("Users", users);
        startActivity(intent2homepage);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mUserView;
        TextView Name;
        TextView Roles;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mUserView = itemView;
            Name = mUserView.findViewById(R.id.textView_Name_list);
            Roles = mUserView.findViewById(R.id.textView_role);
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
}