package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ListUserAdapter;
import com.example.myapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserActivity extends AppCompatActivity {
    private RecyclerView rv;
    private Button btnInsert;
    private Dialog dialog;
    private String tGender;
    private DatabaseReference dbRet= FirebaseDatabase.getInstance().getReference("User");
    private ArrayList<User> listUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        init();
        initBtnInsert();
        initRCV();

    }
    private void initRCV(){
        dbRet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listUser.clear();
                    for (DataSnapshot user: snapshot.getChildren()
                         ) {
                        String userID=user.child("userID").getValue().toString();
                        String userName= Objects.requireNonNull(user.child("userName").getValue()).toString();
                        String email=Objects.requireNonNull(Objects.requireNonNull(user.child("email").getValue()).toString());
                        String gender=(Objects.requireNonNull(user.child("gender").getValue()).toString());
                        String phone=user.child("phone").getValue().toString();
                        String avata=user.child("avata").getValue().toString();
                        String role=user.child("role").getValue().toString();
                        Boolean status=Boolean.parseBoolean(user.child("status").getValue().toString());
                        User mUser=new User(userID,userName,email,gender,phone,avata,role,status);
                        listUser.add(mUser);
                    }
                    setAdapter(listUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void setAdapter(ArrayList<User> listUser){
        LinearLayoutManager vertical = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(vertical);
        ListUserAdapter adapter = new ListUserAdapter((Context) this, (ArrayList<User>) listUser, new ListUserAdapter.IUser() {
            @Override
            public void onDetailCLick(int position) {
                onSetupDetailDiaog(listUser.get(position));
            }
        });
        rv.setAdapter(adapter);
    }
    private void initBtnInsert(){
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetupInsertDialog();
            }
        });
    }
    private void init(){
        rv = findViewById(R.id.userRecyclerView);
        btnInsert=findViewById(R.id.btnInsert);
    }
    private void onSetupInsertDialog(){
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.user_insert);
        Window window = dialog.getWindow();


                window.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                );
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAtributes = window.getAttributes();
                windowAtributes.gravity = Gravity.CENTER;
                window.setAttributes(windowAtributes);
                    if (Gravity.CENTER == Gravity.CENTER) {
                        dialog.setCancelable(true);
                    } else {
                        dialog.setCancelable(false);
                    }
            InsertUser(dialog);
            dialog.show();
    }
    private void InsertUser(Dialog dialog){
        EditText userName=dialog.findViewById(R.id.txtUserName);
        EditText email=dialog.findViewById(R.id.txtEmail);
        RadioGroup gender=dialog.findViewById(R.id.groupRadioGender);
        EditText phone=dialog.findViewById(R.id.txtPhone);
        EditText avata=dialog.findViewById(R.id.txtAvata);
        EditText role=dialog.findViewById(R.id.txtRole);
        Button btnInsert=dialog.findViewById(R.id.btnInsert);
        Button btnCancel=dialog.findViewById(R.id.btnCancel);
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.btnFemale: tGender="Female";break;
                    case R.id.btnMale:tGender="Male";break;
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String autoID=dbRet.push().getKey();
                String tUserName=userName.getText().toString();
                String tEmail=email.getText().toString();
                String tPhone = phone.getText().toString();
                String tAvata=avata.getText().toString();
                String tRole=role.getText().toString();
                User user=new User(autoID,tUserName,tEmail,tGender,tPhone,tAvata,tRole,true);
                dbRet.child(autoID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UserActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("FirebaseErro",e.toString());
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void onSetupDetailDiaog(User user){
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.user_dialog);
        Window window = dialog.getWindow();


        window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAtributes = window.getAttributes();
        windowAtributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAtributes);
        if (Gravity.CENTER == Gravity.CENTER) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }
        EditText userName=dialog.findViewById(R.id.txtUserName);
        EditText email=dialog.findViewById(R.id.txtEmail);
        EditText gender=dialog.findViewById(R.id.txtGender);
        EditText phone=dialog.findViewById(R.id.txtPhone);
        ImageView avata=dialog.findViewById(R.id.imgAvata);
        EditText role=dialog.findViewById(R.id.txtRole);
        Button btnEdit=dialog.findViewById(R.id.btnEdit);
        Button btnDelete=dialog.findViewById(R.id.btnDelete);
        Button btnCancel=dialog.findViewById(R.id.btnCancel);
        userName.setText(user.getUserName());
        email.setText(user.getEmail());
        gender.setText(user.getGender()+"");
        phone.setText(user.getPhone());
        Picasso.with(this).load(user.getAvata())
                .placeholder(R.drawable.icon_loading)
                .error(R.drawable.ic_error)
                .into(avata);
        role.setText(user.getRole());
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String autoID=user.getUserID();
                String tUserName=userName.getText().toString();
                String tEmail=email.getText().toString();

                String tPhone = phone.getText().toString();
                String tAvata=user.getAvata();
                String tRole=role.getText().toString();
                User user=new User(autoID,tUserName,tEmail,tGender,tPhone,tAvata,tRole,true);
                dbRet.child(autoID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UserActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("FirebaseErro",e.toString());
                        dialog.dismiss();
                    }
                });
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRet.child(user.getUserID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UserActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("FirebaseErro",e.toString());
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }



}