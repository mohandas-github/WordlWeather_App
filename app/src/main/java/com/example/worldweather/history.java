package com.example.worldweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class history extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextView text;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_history);

        text=findViewById(R.id.display_history);

        sharedPreferences = getSharedPreferences("history", Context.MODE_PRIVATE);
        String his = sharedPreferences.getString("data", "");
        text.setText(his);

        String pre_data= text.getText().toString();

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                in.putExtra("pre_cname",pre_data);
                startActivity(in);
            }
        });

    }
}
