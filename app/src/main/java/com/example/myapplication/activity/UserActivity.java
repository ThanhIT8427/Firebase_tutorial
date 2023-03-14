package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ListUserAdapter;
import com.example.myapplication.databinding.ActivityUserBinding;
import com.example.myapplication.databinding.UserDialogBinding;
import com.example.myapplication.databinding.UserInsertBinding;
import com.example.myapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class UserActivity extends AppCompatActivity {
    private ActivityUserBinding binding;
    private Dialog dialog;
    private User mainUser;
    private DatabaseReference dbRet= FirebaseDatabase.getInstance().getReference("User");
    private ArrayList<User> listUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mainUser=new User();
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
                        Boolean status=Boolean.parseBoolean(user.child("status").getValue().toString());
                        User mUser=new User(userID,userName,email,gender,phone,avata,status);
                        listUser.add(mUser);
                    }
                    setAdapter(listUser);
                    initSearch(listUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void setAdapter(ArrayList<User> listUser){
        LinearLayoutManager vertical = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.userRecyclerView.setLayoutManager(vertical);
        ListUserAdapter adapter = new ListUserAdapter((Context) this, (ArrayList<User>) listUser, new ListUserAdapter.IUser() {
            @Override
            public void onDetailCLick(int position) {

                mainUser=listUser.get(position);
                onSetupDetailDiaog(mainUser);
            }
        });
        binding.userRecyclerView.setAdapter(adapter);

    }
    private void initSearch(ArrayList<User> users){
        binding.btnSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<User> listSearch=new ArrayList<>();
                String search=binding.searchView.getQuery().toString();
                if(search.equals("")){
                    for (User u:users
                         ) {
                        listSearch.add(u);
                    }
                    setAdapter(listSearch);
                }
                else {
                    for (User u:users
                         ) {
                        if(u.getUserName().contains(search)){
                            listSearch.add(u);

                        }
                    }
                }
                setAdapter(listSearch);
            }
        });
    }
    private void initBtnInsert(){
        binding.btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetupInsertDialog();
            }
        });
    }
    private void init(){

    }
    private void onSetupInsertDialog(){
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        UserInsertBinding viewDataBinding= UserInsertBinding.inflate(LayoutInflater.from(UserActivity.this));
        dialog.setContentView(viewDataBinding.getRoot());
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
            InsertUser(viewDataBinding);
            dialog.show();
    }
    private void InsertUser(UserInsertBinding viewDataBinding){
        viewDataBinding.groupRadioGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.btnFemale: mainUser.setGender("Female");break;
                    case R.id.btnMale:mainUser.setGender("Male");break;
                }
            }
        });
        viewDataBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        viewDataBinding.btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String autoID=dbRet.push().getKey();
                mainUser.setUserID(autoID);
                mainUser.setUserName(viewDataBinding.txtUserName.getText().toString());;
                mainUser.setEmail(viewDataBinding.txtEmail.getText().toString());
                mainUser.setPhone(viewDataBinding.txtPhone.getText().toString());
                mainUser.setAvata(viewDataBinding.txtAvata.getText().toString());
                mainUser.setStatus(true);
                dbRet.child(autoID).setValue(mainUser).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        UserDialogBinding viewDataBinding=UserDialogBinding.inflate(LayoutInflater.from(UserActivity.this));
        dialog.setContentView(viewDataBinding.getRoot());
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
        viewDataBinding.groupRadioGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.btnFemale: mainUser.setGender("Female");break;
                    case R.id.btnMale:mainUser.setGender("Male");break;
                }
            }
        });
        viewDataBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        viewDataBinding.txtUserName.setText(user.getUserName());
        viewDataBinding.txtEmail.setText(user.getEmail());
        if(user.getGender().equals("Male")){
            viewDataBinding.btnMale.setChecked(true);
        }else {
            viewDataBinding.btnFemale.setChecked(true);
        }
        viewDataBinding.txtPhone.setText(user.getPhone());
        Picasso.with(this).load(user.getAvata())
                .placeholder(R.drawable.icon_loading)
                .error(R.drawable.ic_error)
                .into(viewDataBinding.imgAvata);
        viewDataBinding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String autoID=user.getUserID();
                user.setUserName(viewDataBinding.txtUserName.getText().toString());;
                user.setEmail(viewDataBinding.txtEmail.getText().toString());
                user.setPhone(viewDataBinding.txtPhone.getText().toString());
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
        viewDataBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
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