package com.example.jwpackage.JeddahWaterfrontAR;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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


public class BackgroundWorker extends AsyncTask<String, Void, String> {

    Context context;
    AlertDialog alertDialog;
    public static String type;
    public static String user_Email, user_Password, user_Name;

    BackgroundWorker(Context ctx) {
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

            String post_data = "", line = "", service_Id, userEvaluation, userComment, theRating, user_Choice = "";
            String result = "";

            if (type.equalsIgnoreCase("login")) {
                user_Email = params[1];
                user_Password = params[2];
                post_data = URLEncoder.encode("UserEmail", "UTF-8") + "=" + URLEncoder.encode(user_Email, "UTF-8") + "&"
                        + URLEncoder.encode("User_Pass", "UTF-8") + "=" + URLEncoder.encode(user_Password, "UTF-8");
                bufferedWriter.write(post_data);
            } else if (type.equalsIgnoreCase("Register")) {
                user_Name = params[1];
                user_Email = params[2];
                user_Password = params[3];
                post_data = URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(user_Name, "UTF-8") + "&" +
                        URLEncoder.encode("userEmail", "UTF-8") + "=" + URLEncoder.encode(user_Email, "UTF-8") + "&" +
                        URLEncoder.encode("userPass", "UTF-8") + "=" + URLEncoder.encode(user_Password, "UTF-8");
                bufferedWriter.write(post_data);
            } else if (type.equalsIgnoreCase("parkingPlace")) {
                user_Email = params[1];
                String insOrDel = params[2];
                int insOrDelint = Integer.parseInt(insOrDel);
                if (insOrDelint == 1) {
                    post_data = URLEncoder.encode("userEmail", "UTF-8") + "=" + URLEncoder.encode(user_Email, "UTF-8") + "&" +
                            URLEncoder.encode("insOrDel", "UTF-8") + "=" + URLEncoder.encode(insOrDel, "UTF-8");
                } else if (insOrDelint == 2) {
                    String carParkX = params[3];
                    String carParkY = params[4];
                    post_data = URLEncoder.encode("userEmail", "UTF-8") + "=" + URLEncoder.encode(user_Email, "UTF-8") + "&" +
                            URLEncoder.encode("insOrDel", "UTF-8") + "=" + URLEncoder.encode(insOrDel, "UTF-8") + "&" +
                            URLEncoder.encode("carParkX", "UTF-8") + "=" + URLEncoder.encode(carParkX, "UTF-8") + "&" +
                            URLEncoder.encode("carParkY", "UTF-8") + "=" + URLEncoder.encode(carParkY, "UTF-8");
                } else if (insOrDelint == 3) {
                    post_data = URLEncoder.encode("userEmail", "UTF-8") + "=" + URLEncoder.encode(user_Email, "UTF-8") + "&" +
                            URLEncoder.encode("insOrDel", "UTF-8") + "=" + URLEncoder.encode(insOrDel, "UTF-8");
                }
                bufferedWriter.write(post_data);
            } else if (type.equalsIgnoreCase("serviceDetails")) {
                result = "";
                service_Id = params[1];

                post_data = URLEncoder.encode("serviceId", "UTF-8") + "=" + URLEncoder.encode(service_Id, "UTF-8");
                bufferedWriter.write(post_data);
            } else if (type.equalsIgnoreCase("UserRatingService")) {
                result = "";
                String x = params[1];
                String y = params[2];
                userEvaluation = params[3];
                userComment = params[4];
                user_Email = common.sp.getString(ConstantFields.userEmail, "").toString();
                post_data = URLEncoder.encode("serviceX", "UTF-8") + "=" + URLEncoder.encode(x, "UTF-8") + "&" +
                        URLEncoder.encode("serviceY", "UTF-8") + "=" + URLEncoder.encode(y, "UTF-8") + "&" +
                        URLEncoder.encode("userEvaluation", "UTF-8") + "=" + URLEncoder.encode(userEvaluation, "UTF-8") + "&" +
                        URLEncoder.encode("userEmail", "UTF-8") + "=" + URLEncoder.encode(user_Email, "UTF-8") + "&" +
                        URLEncoder.encode("userComment", "UTF-8") + "=" + URLEncoder.encode(userComment, "UTF-8");
                bufferedWriter.write(post_data);
            } else if (type.equalsIgnoreCase("AllRatingService")) {
                result = "";
                service_Id = params[1];

                post_data = URLEncoder.encode("serviceId", "UTF-8") + "=" + URLEncoder.encode(service_Id, "UTF-8");
                bufferedWriter.write(post_data);

            } else if (type.equalsIgnoreCase("Favorites")) {
                result = "";
                String x = params[1];
                String y = params[2];
                user_Choice = params[3];
                int choice = Integer.parseInt(user_Choice);
                user_Email = common.sp.getString(ConstantFields.userEmail, "").toString();

                if (choice == 1) {
                    post_data = URLEncoder.encode("serviceX", "UTF-8") + "=" + URLEncoder.encode(x, "UTF-8") + "&" +
                            URLEncoder.encode("serviceY", "UTF-8") + "=" + URLEncoder.encode(y, "UTF-8") + "&" +
                            URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(user_Email, "UTF-8") + "&" +
                            URLEncoder.encode("userChoice", "UTF-8") + "=" + URLEncoder.encode(user_Choice, "UTF-8");
                } else if (choice == 2) {
                    post_data = URLEncoder.encode("serviceX", "UTF-8") + "=" + URLEncoder.encode(x, "UTF-8") + "&" +
                            URLEncoder.encode("serviceY", "UTF-8") + "=" + URLEncoder.encode(y, "UTF-8") + "&" +
                            URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(user_Email, "UTF-8") + "&" +
                            URLEncoder.encode("userChoice", "UTF-8") + "=" + URLEncoder.encode(user_Choice, "UTF-8");
                } else if (choice == 3) {
                    post_data = URLEncoder.encode("serviceX", "UTF-8") + "=" + URLEncoder.encode(x, "UTF-8") + "&" +
                            URLEncoder.encode("serviceY", "UTF-8") + "=" + URLEncoder.encode(y, "UTF-8") + "&" +
                            URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(user_Email, "UTF-8") + "&" +
                            URLEncoder.encode("userChoice", "UTF-8") + "=" + URLEncoder.encode(user_Choice, "UTF-8");
                }
                bufferedWriter.write(post_data);
            }else if(type.equalsIgnoreCase("FavoritesList"))
            {
                user_Email = common.sp.getString(ConstantFields.userEmail, "").toString();
                post_data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(user_Email, "UTF-8");
                 bufferedWriter.write(post_data);
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

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
    }

    @Override
    protected void onPostExecute(String result) {
        if (type.equalsIgnoreCase("fragment_map")) {
            MapFragment.mf.mapPointers(result);
        }
        if (type.equalsIgnoreCase("login")) {
            String[] s = result.split("-");
            if(s.length > 1){
            if (s[1].equalsIgnoreCase("Login Successfully")) {
                common.editor.putString(ConstantFields.userEmail, user_Email);
                common.editor.putString(ConstantFields.userName, s[0]);
                common.editor.putBoolean(ConstantFields.userIsLoggedIn, true);
                common.editor.commit();

                Intent i = new Intent(context, BarActivity.class);
                context.startActivity(i);
            }} else {
                alertDialog.setMessage(result);
                alertDialog.show();
            }
        } else if (type.equalsIgnoreCase("Register")) {

            if (result.equalsIgnoreCase("New record created successfully")) {
                common.editor.putString(ConstantFields.userEmail, user_Email);
                common.editor.putString(ConstantFields.userName, user_Name);
                common.editor.putBoolean(ConstantFields.userIsLoggedIn, true);
                common.editor.commit();
                Intent i = new Intent(context, BarActivity.class);
                context.startActivity(i);

            } else {
                alertDialog.setMessage(result);
                alertDialog.show();
            }
        } else if (type.equalsIgnoreCase("parkingPlace")) {
            String[] res = result.split("-");
            if (res[0].equalsIgnoreCase("true")) {
                MapFragment.mf.parkingInfo(Double.parseDouble(res[1]), Double.parseDouble(res[2]));
            }

        } else if (type.equalsIgnoreCase("serviceDetails")) {
            String[] arr = result.split("-");
            String s = arr[arr.length - 1];
            if (s.equalsIgnoreCase("Detail Successfully")) {
            }
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}