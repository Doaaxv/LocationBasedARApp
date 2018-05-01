package com.example.jwpackage.JeddahWaterfrontAR;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class event_Fragment extends Fragment {

    public static ArrayList<String> images = new ArrayList<>();
    public static ArrayList<String> names = new ArrayList<>();
    public static ArrayList<String> descriptions = new ArrayList<>();
    public static ArrayList<String> days = new ArrayList<>();
    public static ArrayList<String> times = new ArrayList<>();
    public static ArrayList<String> places = new ArrayList<>();
    public static ArrayList<String> tickets = new ArrayList<>();
    public static RecyclerView myRecyclerView;
    public static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_, container, false);
        context = getContext();
        myRecyclerView = view.findViewById(R.id.recyclerview_id_2);

        String type = "fragment_event";
        BackgroundWorkerTask backgroundWorkerTask = new BackgroundWorkerTask(context);
        backgroundWorkerTask.execute(type);

        return view;
    }

    class BackgroundWorkerTask extends AsyncTask<String, Void, String> {
        Context context;
        AlertDialog alertDialog;
        String type;
        String line, result = "";

        BackgroundWorkerTask(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {

            type = params[0];
            String PhpUrl = "https://ulotrichous-railroa.000webhostapp.com/connect/" + type + ".php";
            URL url = null;

            try {

                url = new URL(PhpUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                result = "";

                bufferedWriter.flush();
                bufferedWriter.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Login Status");
        }

        @Override
        protected void onPostExecute(String result) {

            if (!result.equals(null)) {

                result = result.trim();
                String[] resultArr = result.split("_");

                for (int i = 0; i <= resultArr.length - 7; i = i + 7) {
                    images.add(resultArr[i]);
                    names.add(resultArr[i + 1]);
                    descriptions.add(resultArr[i + 2]);
                    days.add(resultArr[i + 3]);
                    times.add(resultArr[i + 4]);
                    places.add(resultArr[i + 5]);
                    tickets.add(resultArr[i + 6]);
                }

                eventsAdapter myAdapter = new eventsAdapter(context, images, names, descriptions, days, times, places, tickets);
                myRecyclerView.setLayoutManager(new GridLayoutManager(context, 1));
                myRecyclerView.setAdapter(myAdapter);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
