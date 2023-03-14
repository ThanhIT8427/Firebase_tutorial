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
import com.example.myapplication.models.Restaurant;
import com.example.myapplication.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListStoreAdapter extends RecyclerView.Adapter<ListStoreAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<Restaurant> restaurants;
    private IRestaurant restaurant;

    public interface IRestaurant{
        public void onDetailCLick(int position);
    }

    public ListStoreAdapter(Context context, ArrayList<Restaurant> restaurants,IRestaurant restaurant) {
        this.context = context;
        this.restaurants = restaurants;
        this.restaurant=restaurant;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.storeName.setText(restaurants.get(i).getResName());
        holder.storeAddress.setText(restaurants.get(i).getResAddress());
        holder.ratingBar.setRating(restaurants.get(i).getResRate());
        Picasso.with(context).load(restaurants.get(i).getResAvata())

                .placeholder(R.drawable.icon_loading)
                .error(R.drawable.ic_error)
                .into(holder.imgStore);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restaurant.onDetailCLick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView storeName, storeAddress;
        ImageView imgStore;
        RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.storeName);
            storeAddress = itemView.findViewById(R.id.storeAddress);
            ratingBar=itemView.findViewById(R.id.hotelRating);
            imgStore=itemView.findViewById(R.id.imageView);
        }
    }
}