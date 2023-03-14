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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ListStoreAdapter;
import com.example.myapplication.adapter.ListUserAdapter;
import com.example.myapplication.databinding.ActivityStoreBinding;
import com.example.myapplication.databinding.StoreDialogBinding;
import com.example.myapplication.databinding.StoreInsertBinding;
import com.example.myapplication.models.Restaurant;
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
import java.util.List;
import java.util.Objects;

public class StoreActivity extends AppCompatActivity {
    private ActivityStoreBinding binding;
    private Dialog dialog;
    private DatabaseReference dbRet= FirebaseDatabase.getInstance().getReference("Restaurant");
    private ArrayList<Restaurant> listRestaurant = new ArrayList<>();
    private Restaurant restaurantMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityStoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        restaurantMain=new Restaurant();
        init();
        initBtnInsert();
        initRCV();

    }

    private void initRCV(){
        dbRet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listRestaurant.clear();
                    for (DataSnapshot res: snapshot.getChildren()
                    ) {
                        String resID=res.child("resID").getValue().toString();
                        String resName= Objects.requireNonNull(res.child("resName").getValue()).toString();
                        Integer resRate=Integer.parseInt(res.child("resRate").getValue().toString());
                        String resType=res.child("resType").getValue().toString();
                        String resAddress=res.child("resAddress").getValue().toString();
                        String resAvata=res.child("resAvata").getValue().toString();
                        Restaurant mRestaurant=new Restaurant(resID,resName,resRate,resAvata,resType,resAddress);
                        listRestaurant.add(mRestaurant);

                    }
                    for (Restaurant u:listRestaurant
                         ) {
                        Log.d("Rsava",u.getResAvata());
                    }
                    setAdapter(listRestaurant);
                    initSearch(listRestaurant);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void setAdapter(ArrayList<Restaurant> listRestaurant){
        LinearLayoutManager storeVertical = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.storeRecyclerView.setLayoutManager(storeVertical);
        ListStoreAdapter adapter = new ListStoreAdapter(this, listRestaurant, new ListStoreAdapter.IRestaurant() {
            @Override
            public void onDetailCLick(int position) {
                restaurantMain=listRestaurant.get(position);
                onSetupDetailDiaog(restaurantMain);
            }
        });
        binding.storeRecyclerView.setAdapter(adapter);
    }
    private void initSearch(ArrayList<Restaurant> restaurants){
        binding.btnSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Restaurant> listSearch=new ArrayList<>();
                String search=binding.searchView.getQuery().toString();
                if(search.equals("")){
                    for (Restaurant u:restaurants
                         ) {
                        listSearch.add(u);
                    }
                }else {
                    for (Restaurant u: restaurants
                         ) {
                        if(u.getResName().contains(search)){
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
        StoreInsertBinding viewDataBinding=StoreInsertBinding.inflate(LayoutInflater.from(StoreActivity.this));
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
        InsertRestaurant(viewDataBinding);
        dialog.show();
    }
    private void InsertRestaurant(StoreInsertBinding viewDataBinding){
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
                restaurantMain.setResID(autoID);
                restaurantMain.setResName(viewDataBinding.txtFullName.getText().toString());
                int c=(int)Double.parseDouble(viewDataBinding.storeRating.getRating()+"");
                restaurantMain.setResRate(c);
                restaurantMain.setResType(viewDataBinding.txtKindofF.getText().toString());
                restaurantMain.setResAddress(viewDataBinding.txtAddress.getText().toString());
                restaurantMain.setResAvata(viewDataBinding.txtAvata.getText().toString());
                dbRet.child(autoID).setValue(restaurantMain).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(StoreActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StoreActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("FirebaseErro",e.toString());
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void onSetupDetailDiaog(Restaurant restaurantMain){
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        StoreDialogBinding viewDataBinding=StoreDialogBinding.inflate(LayoutInflater.from(StoreActivity.this));
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
        viewDataBinding.txtFullName.setText(restaurantMain.getResName());
        viewDataBinding.storeRating.setRating(restaurantMain.getResRate());
        viewDataBinding.txtKindofF.setText(restaurantMain.getResType());
        viewDataBinding.txtAddress.setText(restaurantMain.getResAddress());
        viewDataBinding.txtAvata.setText(restaurantMain.getResAvata());
        Picasso.with(this).load(restaurantMain.getResAvata())
                .placeholder(R.drawable.icon_loading)
                .error(R.drawable.ic_error)
                .into(viewDataBinding.imgAvata);
        viewDataBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        viewDataBinding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                restaurantMain.setResName(viewDataBinding.txtFullName.getText().toString());
                int c=(int)Double.parseDouble(viewDataBinding.storeRating.getRating()+"");
                restaurantMain.setResRate(c);
                restaurantMain.setResType(viewDataBinding.txtKindofF.getText().toString());
                restaurantMain.setResAddress(viewDataBinding.txtAddress.getText().toString());
                restaurantMain.setResAvata(viewDataBinding.txtAvata.getText().toString());
                dbRet.child(restaurantMain.getResID()).setValue(restaurantMain).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(StoreActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StoreActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("FirebaseErro",e.toString());
                        dialog.dismiss();
                    }
                });
            }
        });
        viewDataBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRet.child(restaurantMain.getResID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(StoreActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StoreActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("FirebaseErro",e.toString());
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }
}