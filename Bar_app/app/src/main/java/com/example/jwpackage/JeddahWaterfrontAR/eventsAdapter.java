package com.example.jwpackage.JeddahWaterfrontAR;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.jwpackage.JeddahWaterfrontAR.event_Fragment.days;
import static com.example.jwpackage.JeddahWaterfrontAR.event_Fragment.descriptions;
import static com.example.jwpackage.JeddahWaterfrontAR.event_Fragment.images;
import static com.example.jwpackage.JeddahWaterfrontAR.event_Fragment.names;
import static com.example.jwpackage.JeddahWaterfrontAR.event_Fragment.places;
import static com.example.jwpackage.JeddahWaterfrontAR.event_Fragment.tickets;
import static com.example.jwpackage.JeddahWaterfrontAR.event_Fragment.times;

public class eventsAdapter extends RecyclerView.Adapter<eventsAdapter.eventsHolder> {

    private Context mContext;
    ArrayList<String> EventImages = images;
    ArrayList<String> EventNames = names;
    ArrayList<String> EventDescriptions = descriptions;
    ArrayList<String> EventDays = days;
    ArrayList<String> EventTimes = times;
    ArrayList<String> EventPlaces = places;
    ArrayList<String> EventTickets = tickets;

    public eventsAdapter(Context mContext, ArrayList<String> eventImages, ArrayList<String> eventNames, ArrayList<String> eventDescriptions, ArrayList<String> eventDays, ArrayList<String> eventTimes, ArrayList<String> eventPlaces, ArrayList<String> eventTickets) {
        this.mContext = mContext;
        EventImages = eventImages;
        EventNames = eventNames;
        EventDescriptions = eventDescriptions;
        EventDays = eventDays;
        EventTimes = eventTimes;
        EventPlaces = eventPlaces;
        EventTickets = eventTickets;
    }

    @Override
    public eventsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_events, parent, false);
        return new eventsHolder(view);
    }

    @Override
    public void onBindViewHolder(eventsHolder holder, int position) {

        holder.event_Name.setText(EventNames.get(position));
        holder.event_Time.setText(EventTimes.get(position));
        holder.event_Day.setText(EventDays.get(position));
        holder.event_Descriptions.setText(EventDescriptions.get(position));
        holder.event_Place.setText(EventPlaces.get(position));
        holder.event_Ticket.setText(EventTickets.get(position));
        holder.event_Img.setImageResource(mContext.getResources().getIdentifier(EventImages.get(position), "drawable", "com.example.jwpackage.JeddahWaterfrontAR"));
}


    @Override
    public int getItemCount() {
        return EventNames.size();
    }

    public static class eventsHolder extends RecyclerView.ViewHolder {

        TextView event_Name, event_Time, event_Day, event_Descriptions, event_Place, event_Ticket;
        ImageView event_Img;

        public eventsHolder(View itemView) {
            super(itemView);
            event_Img = itemView.findViewById(R.id.event_img_id);
            event_Name = itemView.findViewById(R.id.event_name_id);
            event_Time = itemView.findViewById(R.id.eventTime);
            event_Day = itemView.findViewById(R.id.eventDay);
            event_Descriptions = itemView.findViewById(R.id.eventDesc);
            event_Place = itemView.findViewById(R.id.eventPlace);
            event_Ticket = itemView.findViewById(R.id.eventTicket);

        }
    }
}
