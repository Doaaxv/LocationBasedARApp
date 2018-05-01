package com.example.jwpackage.JeddahWaterfrontAR;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class callsFragment extends Fragment {

    String numberslected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String numberarray[] = {"999", "993", "997", "998", "012 614 9999"};
        String name[] = {"Police", "Traffic", "Ambulance", "Firefighting", "Jeddah Municipality Control tower"};


        ListView listViewofemergency;
        Context ctx;

        View v = inflater.inflate(R.layout.fragment_soscalls, container, false);
        ctx = getContext();

        listViewofemergency = v.findViewById(R.id.listnumber);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, name);
        listViewofemergency.setAdapter(adapter);
        listViewofemergency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i <= 5; i++) {
                    if (position == i)
                        numberslected = numberarray[i];
                }
                Intent phone = new Intent(Intent.ACTION_DIAL);
                phone.setData(Uri.parse("tel:" + numberslected));
                startActivity(phone);
            }
        });

        return v;
    }

}
