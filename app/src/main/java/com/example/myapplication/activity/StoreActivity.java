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
import com.example.myapplication.adapter.ListStoreAdapter;
import com.example.myapplication.adapter.ListUserAdapter;
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
    private RecyclerView rv;
    private Button btnInsert;
    private Dialog dialog;
    private DatabaseReference dbRet= FirebaseDatabase.getInstance().getReference("Restaurant");
    private ArrayList<Restaurant> listRestaurant = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
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
                        String resMap=res.child("resMap").getValue().toString();
                        String resAvata=res.child("resAvata").getValue().toString();
                        Restaurant mRestaurant=new Restaurant(resID,resName,resRate,resAvata,resType,resAddress,resMap);
                        listRestaurant.add(mRestaurant);
                    }
                    setAdapter(listRestaurant);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void setAdapter(ArrayList<Restaurant> listRestaurant){
        LinearLayoutManager storeVertical = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView srv = findViewById(R.id.storeRecyclerView);
        srv.setLayoutManager(storeVertical);
        ListStoreAdapter adapter = new ListStoreAdapter(this, listRestaurant, new ListStoreAdapter.IRestaurant() {
            @Override
            public void onDetailCLick(int position) {
                onSetupDetailDiaog(listRestaurant.get(position));
            }
        });
        srv.setAdapter(adapter);
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
        rv = findViewById(R.id.storeRecyclerView);
        btnInsert=findViewById(R.id.btnInsert);
    }
    private void onSetupInsertDialog(){
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.store_insert);
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
        EditText txtLong=dialog.findViewById(R.id.txtLong);
        EditText txtLat=dialog.findViewById(R.id.txtLat);
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
                String tUserName=resName.getText().toString();
                Integer tRate=Integer.parseInt(txtRate.getText().toString());
                String tKindofF = txtKindofF.getText().toString();
                String tAddress=txtAddress.getText().toString();
                String tAvata=txtAvata.getText().toString();
                String tMap=txtLong.getText().toString()+" - "+txtLat.getText().toString();
                Restaurant mRestaurant=new Restaurant(autoID,tUserName,tRate,tAvata,tKindofF,tAddress,tMap);
                assert autoID != null;
                dbRet.child(autoID).setValue(mRestaurant).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void onSetupDetailDiaog(Restaurant restaurant){
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.store_dialog);
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
        EditText resName=dialog.findViewById(R.id.txtFullName);
        EditText txtRate=dialog.findViewById(R.id.txtRate);
        EditText txtKindofF=dialog.findViewById(R.id.txtKindofF);
        EditText txtAddress=dialog.findViewById(R.id.txtAddress);
        ImageView txtAvata=dialog.findViewById(R.id.imgAvata);
        EditText txtLong=dialog.findViewById(R.id.txtLong);
        EditText txtLat=dialog.findViewById(R.id.txtLat);
        Button btnEdit=dialog.findViewById(R.id.btnEdit);
        Button btnDelete=dialog.findViewById(R.id.btnDelete);
        Button btnCancel=dialog.findViewById(R.id.btnCancel);

        resName.setText(restaurant.getResName());
        txtRate.setText(restaurant.getResRate()+"");
        txtKindofF.setText(restaurant.getResType());
        txtAddress.setText(restaurant.getResAddress());

        Picasso.with(this).load(restaurant.getResAvata())
                .placeholder(R.drawable.icon_loading)
                .error(R.drawable.ic_error)
                .into(txtAvata);
        String[] arrOfStr = restaurant.getResMap().split("-", 2);
        txtLong.setText(arrOfStr[0]);
        txtLat.setText(arrOfStr[1]);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String autoID=restaurant.getResID();
                String tResName=resName.getText().toString();
                Integer tRate=Integer.parseInt(txtRate.getText().toString());
                String tKindofF = txtKindofF.getText().toString();
                String tAvata=restaurant.getResAvata();
                String tAddress=txtAddress.getText().toString();
                String tMap=txtLong.getText()+" - "+txtLat.getText();
               Restaurant restaurant1=new Restaurant(autoID,tResName,tRate,tAvata,tKindofF,tAddress,tMap);
                dbRet.child(autoID).setValue(restaurant1).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRet.child(restaurant.getResID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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