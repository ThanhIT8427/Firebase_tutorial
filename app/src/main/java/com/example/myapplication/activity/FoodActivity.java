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
import com.example.myapplication.adapter.ListFoodAdapter;
import com.example.myapplication.adapter.ListStoreAdapter;
import com.example.myapplication.databinding.ActivityFoodBinding;
import com.example.myapplication.databinding.FoodDialogBinding;
import com.example.myapplication.databinding.FoodInsertBinding;
import com.example.myapplication.models.Food;
import com.example.myapplication.models.Restaurant;
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

public class FoodActivity extends AppCompatActivity {
    private ActivityFoodBinding binding;
    private Dialog dialog;
    private DatabaseReference dbRet= FirebaseDatabase.getInstance().getReference("Foods");
    private ArrayList<Food> listFood = new ArrayList<>();
    private Food foodMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFoodBinding.inflate(getLayoutInflater());
        foodMain=new Food();
        setContentView(binding.getRoot());
        init();
        initBtnInsert();
        initRCV();

    }
    private void init(){

    }
    private void initSearch(ArrayList<Food> foods){
        binding.btnSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Food> listFood=new ArrayList<>();
                String search=binding.searchView.getQuery().toString();
                if(search.equals("")){
                    for (Food u:foods
                    ) {
                        listFood.add(u);
                    }
                }else {
                    for (Food u: foods
                    ) {
                        if(u.getFoodName().contains(search)){
                            listFood.add(u);
                        }
                    }

                }
                setAdapter(listFood);
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
    private void onSetupInsertDialog(){
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        FoodInsertBinding viewDataBinding=FoodInsertBinding.inflate(LayoutInflater.from(FoodActivity.this));
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
    private void InsertRestaurant(FoodInsertBinding viewDataBinding){
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
                foodMain.setFoodID(autoID);
                foodMain.setFoodName(viewDataBinding.txtFullName.getText().toString());
                foodMain.setFoodRate(viewDataBinding.storeRating.getRating());
                foodMain.setFoodType(viewDataBinding.txtKindofF.getText().toString());
                foodMain.setFoodAvata(viewDataBinding.txtAvata.getText().toString());

                dbRet.child(foodMain.getFoodID()).setValue(foodMain).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FoodActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FoodActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("FirebaseErro",e.toString());
                        dialog.dismiss();
                    }
                });
            }
        });
    }
    private void initRCV(){
        dbRet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listFood.clear();
                    for (DataSnapshot food: snapshot.getChildren()
                    ) {
                        String foodID=food.child("foodID").getValue().toString();
                        String foodName= Objects.requireNonNull(food.child("foodName").getValue()).toString();
                        Float foodRate=Float.parseFloat(food.child("foodRate").getValue().toString());
                        String foodType=food.child("foodType").getValue().toString();
                        String foodAvata=food.child("foodAvata").getValue().toString();
                        Food mfood=new Food(foodID,foodName,foodRate,foodAvata,foodType);
                        listFood.add(mfood);
                    }
                    setAdapter(listFood);
                    initSearch(listFood);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void setAdapter(ArrayList<Food> listFood){
        LinearLayoutManager storeVertical = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.foodRecyclerView.setLayoutManager(storeVertical);
        ListFoodAdapter adapter = new ListFoodAdapter(this, listFood, new ListFoodAdapter.IFood() {
            @Override
            public void onDetailCLick(int position) {
                foodMain=listFood.get(position);
                onSetupDetailDiaog(foodMain);
            }
        });
        binding.foodRecyclerView.setAdapter(adapter);
    }
    private void onSetupDetailDiaog(Food food){
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        FoodDialogBinding viewDataBinding=FoodDialogBinding.inflate(LayoutInflater.from(FoodActivity.this));

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
        viewDataBinding.txtFullName.setText(food.getFoodName());
        viewDataBinding.storeRating.setRating(food.getFoodRate());
        viewDataBinding.txtKindofF.setText(food.getFoodType());
        Picasso.with(this).load(food.getFoodAvata())
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
                food.setFoodName(viewDataBinding.txtFullName.getText().toString());
                food.setFoodRate(viewDataBinding.storeRating.getRating());
                food.setFoodType(viewDataBinding.txtKindofF.getText().toString());
                dbRet.child(food.getFoodID()).setValue(food).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FoodActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FoodActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("FirebaseErro",e.toString());
                        dialog.dismiss();
                    }
                });
            }
        });
        viewDataBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRet.child(food.getFoodID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FoodActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FoodActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("FirebaseErro",e.toString());
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }
}