package com.example.worldweather;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Permission;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.Date;


public class MainActivity extends AppCompatActivity {


    Button history;
    ImageView search,condition_image;
    EditText city_name;
    TextView city, show_temp,condition_text, min, max, sunrise_time, condition, sunset_time, wind, pressure, feellike,humidity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //check the connection, if it s not set call showCustomDialog() method to show alert message
        if(!isConnected(MainActivity.this))
        {
            showCustomDialog();
        }

        search = findViewById(R.id.search);
        city_name=findViewById(R.id.input_city);
        city= findViewById(R.id.city_name);

        show_temp=findViewById(R.id.show_temp);
        condition_text=findViewById(R.id.condition_text);
        condition_image=findViewById(R.id.condition_img);


        sunrise_time= findViewById(R.id.sunrise_time);
        sunset_time= findViewById(R.id.sunset_time);
        wind= findViewById(R.id.wind_speed);
        pressure= findViewById(R.id.pressure);
        feellike= findViewById(R.id.feellike);
        humidity= findViewById(R.id.humidity);

        history=findViewById(R.id.get_history);


        Intent get_previous=getIntent();
        String pre_data= get_previous.getStringExtra("pre_cname");
        city_name.setText(pre_data);

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in =new Intent(MainActivity.this,history.class);

                startActivity(in);
            }
        });



        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cname = city_name.getText().toString().trim();

                String appid= "f59772b2433025ba8088e34088b2a99c";     //setting the appid
                String url= "https://api.openweathermap.org/data/2.5/weather?q="+cname+"&appid="+appid;  //setting the url




                if(cname.equals("")){
                    Toast.makeText(MainActivity.this, "Enter City Name", Toast.LENGTH_SHORT).show();
                }

               // creating the shared preference
                SharedPreferences sharedPreferences= getSharedPreferences("history",MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("data",cname);
                edit.apply();
                edit.commit();



                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject object =response.getJSONObject("main");

                            //getting the weather condition that is in the array
                            JSONObject object_condition =response.getJSONArray("weather").getJSONObject(0);


                            String cond=object_condition.getString("main");
                            condition_text.setText(cond);

                            //we provide some symbol for the weather condition
                            //if condition is out of our scope display not found image

                            if(cond.equals("Clouds"))
                            {
                                condition_image.setImageResource(R.drawable.clouds);
                            }
                            else if(cond.equals("Dust"))
                            {
                                condition_image.setImageResource(R.drawable.dust);
                            }
                            else if(cond.equals("Clear"))
                            {
                                condition_image.setImageResource(R.drawable.clear);
                            }
                            else if(cond.equals("Rain"))
                            {
                                condition_image.setImageResource(R.drawable.rain);
                            }
                            else if(cond.equals("Haze"))
                            {
                                condition_image.setImageResource(R.drawable.haze);
                            }
                            else if(cond.equals("Drizzle"))
                            {
                                condition_image.setImageResource(R.drawable.drizzle);
                            }
                            else if(cond.equals("Smoke"))
                            {
                                condition_image.setImageResource(R.drawable.smoke);
                            }

                            else if(cond.equals("Mist"))
                            {
                                condition_image.setImageResource(R.drawable.mist);
                            }
                            else
                            {
                                condition_image.setImageResource(R.drawable.nofound);
                            }

                            //set the city name in the label
                            city.setText("CITY: " +cname.toUpperCase());


                            //get the temperature
                            String temp =object.getString("temp");
                            Double temp2 = Double.parseDouble(temp) - 273.15;
                            show_temp.setText(temp2.toString().substring(0,5) + " °C");


                            //get the feel like temperature
                            String feel_like = object.getString("feels_like");
                            Double feel_like2 =Double.parseDouble(feel_like) - 273.15;
                            feellike.setText(feel_like2.toString().substring(0,5) + "°C");

                            //get the pressure
                            String get_pressure = object.getString("pressure");
                            pressure.setText(get_pressure +" hPa");

                            //get the humidity
                            String get_humidity = object.getString("humidity");
                            humidity.setText(get_humidity +"%");



                            JSONObject object2 =response.getJSONObject("wind");

                            //get the wind
                            //speed is in the meter/sec
                            String get_wind = object2.getString("speed");
                            wind.setText(get_wind +" m/sec");


                            JSONObject object3 =response.getJSONObject("sys");

                            //get the sunrise time
                            String get_sunrise = object3.getString("sunrise");
                            Long ln=Long.parseLong(get_sunrise);

                            Date date_rise=new Date(ln *1000);
                            SimpleDateFormat format_rise = new SimpleDateFormat("HH:mm");
                            String mydate_rise = format_rise.format(date_rise);  //the time in IST(Indian Standard Time)
                            sunrise_time.setText(mydate_rise +" IST");

                            //get the sunset time
                            String get_sunset = object3.getString("sunset");
                            Long ln2=Long.parseLong(get_sunset);

                            Date date_set = new Date(ln2 *1000);
                            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                            String mydate_set = format.format(date_set);    //the time in IST(Indian Standard Time)
                            sunset_time.setText(mydate_set+" IST");


                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "City Not Found", Toast.LENGTH_SHORT).show();

                    }
                });
                queue.add(request);
            }
        });

    }


    //method that check the wifi and internet connection
    private boolean isConnected(MainActivity mainActivity) {

        ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected()))
        {
            return true;
        }
        else{

            return  false;
        }
    }

    //method that show the alert dialog when there is no internet connection
    private void showCustomDialog()
    {
        new  AlertDialog.Builder(MainActivity.this)
                .setIcon(R.drawable.no_internet)
                .setTitle("NO INTERNET CONNECTION")
                .setMessage("Check Your Internet Connection To Proceed")
                .setPositiveButton("CONNECT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                        finish();

                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            }
        }).show();

    }


}

