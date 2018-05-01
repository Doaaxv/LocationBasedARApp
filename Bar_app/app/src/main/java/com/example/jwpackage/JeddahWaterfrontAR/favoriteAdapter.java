package com.example.jwpackage.JeddahWaterfrontAR;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.jwpackage.JeddahWaterfrontAR.favoritesFragment.servicesId;
import static com.example.jwpackage.JeddahWaterfrontAR.favoritesFragment.servicesName;
import static com.example.jwpackage.JeddahWaterfrontAR.favoritesFragment.servicesX;
import static com.example.jwpackage.JeddahWaterfrontAR.favoritesFragment.servicesY;
import static com.example.jwpackage.JeddahWaterfrontAR.favoritesFragment.servisesImg;


public class favoriteAdapter extends RecyclerView.Adapter<favoriteAdapter.favoriteHolder> {

    private Context mContext;
    ArrayList<String> services_Id = servicesId;
    ArrayList<String> services_Name = servicesName;
    ArrayList<String> servises_Img = servisesImg;
    ArrayList<String> servises_X = servicesX;
    ArrayList<String> servises_Y = servicesY;

    public favoriteAdapter(Context mContext, ArrayList<String> services_Id, ArrayList<String> services_Name, ArrayList<String> servises_Img, ArrayList<String> servises_X, ArrayList<String> servises_Y) {
        this.mContext = mContext;
        this.services_Id = services_Id;
        this.services_Name = services_Name;
        this.servises_Img = servises_Img;
        this.servises_X = servises_X;
        this.servises_Y = servises_Y;
    }
    @Override
    public favoriteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_favorites, parent, false);
        return new favoriteHolder(view);
    }

    @Override
    public void onBindViewHolder(favoriteAdapter.favoriteHolder holder, final int position) {

        holder.fav_serviceName.setText(services_Name.get(position));
        holder.fav_serviceImg.setImageResource(mContext.getResources().getIdentifier(servises_Img.get(position), "drawable", "com.example.jwpackage.JeddahWaterfrontAR"));

    }

    @Override
    public int getItemCount() {
        return services_Name.size();
    }


    public static class favoriteHolder extends RecyclerView.ViewHolder {

        TextView fav_serviceName;
        ImageView fav_serviceImg;
        CardView cardView;

        public favoriteHolder(View itemView) {
            super(itemView);
            fav_serviceName = itemView.findViewById(R.id.service_name_id);
            fav_serviceImg = itemView.findViewById(R.id.service_img_id);
            cardView = itemView.findViewById(R.id.cardView_id);

        }
    }
}
