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
import java.net.URLEncoder;
import java.util.ArrayList;


public class favoritesFragment extends Fragment {

    public static ArrayList<String> servicesId = new ArrayList<>();
    public static ArrayList<String> servicesX = new ArrayList<>();
    public static ArrayList<String> servicesY = new ArrayList<>();
    public static ArrayList<String> servicesName = new ArrayList<>();
    public static ArrayList<String> servisesImg = new ArrayList<>();
    public static RecyclerView myRecyclerView;
    public static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        context = getContext();
        myRecyclerView = view.findViewById(R.id.recyclerview_id);

        String type = "FavoritesList";
        BackgroundWorkerTask backgroundWorkerTask = new BackgroundWorkerTask(context);
        backgroundWorkerTask.execute(type);


        return view;
    }

    class BackgroundWorkerTask extends AsyncTask<String, Void, String> {
        Context context;
        AlertDialog alertDialog;
        String type;
        String user_Email, post_data, line, result = "";

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
                user_Email = common.sp.getString(ConstantFields.userEmail, "").toString();
                post_data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(user_Email, "UTF-8");

                bufferedWriter.write(post_data);
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
                String[] resultArr = result.split("-");

                for (int i = 0; i <= resultArr.length - 5; i = i + 5) {
                    servicesId.add(resultArr[i]);
                    servicesName.add(resultArr[i + 1]);
                    servisesImg.add(resultArr[i + 2]);
                    servicesX.add(resultArr[i + 3]);
                    servicesY.add(resultArr[i + 4]);
                }

                favoriteAdapter myAdapter = new favoriteAdapter(context, servicesId, servicesName, servisesImg, servicesX, servicesY);

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
