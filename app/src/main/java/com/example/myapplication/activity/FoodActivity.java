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
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ListFoodAdapter;
import com.example.myapplication.adapter.ListStoreAdapter;
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
    private RecyclerView rv;
    private Button btnInsert;
    private Dialog dialog;
    private DatabaseReference dbRet= FirebaseDatabase.getInstance().getReference("Foods");
    private ArrayList<Food> listFood = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        init();
        initBtnInsert();
        initRCV();

    }
    private void init(){
        rv = findViewById(R.id.storeRecyclerView);
        btnInsert=findViewById(R.id.btnInsert);
    }
    private void initBtnInsert(){
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetupInsertDialog();
            }
        });
    }
    private void onSetupInsertDialog(){
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.food_insert);
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
        InsertRestaurant(dialog);
        dialog.show();
    }
    private void InsertRestaurant(Dialog dialog){
        EditText resName=dialog.findViewById(R.id.txtFullName);
        EditText txtRate=dialog.findViewById(R.id.txtRate);
        EditText txtKindofF=dialog.findViewById(R.id.txtKindofF);
        EditText txtAddress=dialog.findViewById(R.id.txtAddress);
        EditText txtAvata=dialog.findViewById(R.id.txtAvata);
        Button btnInsert=dialog.findViewById(R.id.btnInsert);
        Button btnCancel=dialog.findViewById(R.id.btnCancel);
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
                String tFoodName=resName.getText().toString();
                Integer tRate=Integer.parseInt(txtRate.getText().toString());
                String tKindofF = txtKindofF.getText().toString();
                String tAvata=txtAvata.getText().toString();
                Food mFood=new Food(autoID,tFoodName,tRate,tAvata,tKindofF);
                assert autoID != null;
                dbRet.child(autoID).setValue(mFood).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                        Integer foodRate=Integer.parseInt(food.child("foodRate").getValue().toString());
                        String foodType=food.child("foodType").getValue().toString();
                        String foodAvata=food.child("foodAvata").getValue().toString();
                        Food mfood=new Food(foodID,foodName,foodRate,foodAvata,foodType);
                        listFood.add(mfood);
                    }
                    setAdapter(listFood);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void setAdapter(ArrayList<Food> listFood){
        LinearLayoutManager storeVertical = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView srv = findViewById(R.id.foodRecyclerView);
        srv.setLayoutManager(storeVertical);
        ListFoodAdapter adapter = new ListFoodAdapter(this, listFood, new ListFoodAdapter.IFood() {
            @Override
            public void onDetailCLick(int position) {
                onSetupDetailDiaog(listFood.get(position));
            }
        });
        srv.setAdapter(adapter);
    }
    private void onSetupDetailDiaog(Food food){
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.food_dialog);
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
        EditText foodName=dialog.findViewById(R.id.txtFullName);
        EditText foodRate=dialog.findViewById(R.id.txtRate);
        EditText txtKindofF=dialog.findViewById(R.id.txtKindofF);
        ImageView txtAvata=dialog.findViewById(R.id.imgAvata);
        Button btnEdit=dialog.findViewById(R.id.btnEdit);
        Button btnDelete=dialog.findViewById(R.id.btnDelete);
        Button btnCancel=dialog.findViewById(R.id.btnCancel);

        foodName.setText(food.getFoodName());
        foodRate.setText(food.getFoodRate()+"");
        txtKindofF.setText(food.getFoodType());
        Picasso.with(this).load(food.getFoodAvata())
                .placeholder(R.drawable.icon_loading)
                .error(R.drawable.ic_error)
                .into(txtAvata);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String autoID=food.getFoodID();
                String tResName=foodName.getText().toString();
                Integer tRate=Integer.parseInt(foodRate.getText().toString());
                String tKindofF = txtKindofF.getText().toString();
                String tAvata=food.getFoodAvata();
                Food food1=new Food(autoID,tResName,tRate,tAvata,tKindofF);
                dbRet.child(autoID).setValue(food1).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        btnDelete.setOnClickListener(new View.OnClickListener() {
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