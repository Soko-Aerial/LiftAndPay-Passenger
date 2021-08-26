package com.example.liftandpay_passenger.overpass;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView txtV;
    private List<overpassModel> element;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://overpass-api.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        modelface modelface = retrofit.create(modelface.class);

        Call<model> call = modelface.getData();


        call.enqueue(new Callback<model>() {
            @Override
            public void onResponse(Call<model> call, Response<model> response) {
                if (response.code() == 200)
                {
                    txtV.setText("Checked " + response.body().getElements().get(0).getLat());
                }
                else
                {
                    txtV.setText("002"+response.code());
                }
            }

            @Override
            public void onFailure(Call<model> call, Throwable t) {
                txtV.setText("003");
            }
        });


    }


}