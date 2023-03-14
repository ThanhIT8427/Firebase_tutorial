package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Food;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListFoodAdapter extends RecyclerView.Adapter<ListFoodAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Food> foods;
    private IFood iFood;

    public interface IFood{
        public void onDetailCLick(int position);
    }
    public ListFoodAdapter(Context context, ArrayList<Food> foods,IFood iFood) {
        this.context = context;
        this.foods = foods;
        this.iFood=iFood;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.foodName.setText(foods.get(i).getFoodName());
        holder.foodId.setText(foods.get(i).getFoodType());
        holder.ratingBar.setRating(foods.get(i).getFoodRate());
        Picasso.with(context).load(foods.get(i).getFoodAvata())

                .placeholder(R.drawable.icon_loading)
                .error(R.drawable.ic_error)
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iFood.onDetailCLick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodId;
        RatingBar ratingBar;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodId = itemView.findViewById(R.id.foodId);
            ratingBar=itemView.findViewById(R.id.hotelRating);
            imageView=itemView.findViewById(R.id.imageView);
        }
    }
}