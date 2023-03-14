package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<User> users;
    private IUser iUser;
    public interface IUser{
        public void onDetailCLick(int position);
    }

    public ListUserAdapter(Context context, ArrayList<User> users,IUser iuser) {
        this.context = context;
        this.users = users;
        this.iUser=iuser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_lists_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        User user=users.get(i);
        holder.fullName.setText(user.getUserName());
        holder.email.setText(user.getEmail());
        Picasso.with(context).load(user.getAvata())
                .placeholder(R.drawable.icon_loading)
                .error(R.drawable.ic_error)
                .into(holder.imgAvata);
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iUser.onDetailCLick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, email;
        ImageView imgAvata;
        Button detail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.txtFullname);
            email = itemView.findViewById(R.id.txtEmail);
            detail=itemView.findViewById(R.id.btnDetail);
            imgAvata=itemView.findViewById(R.id.imgAvata);
        }
    }
}