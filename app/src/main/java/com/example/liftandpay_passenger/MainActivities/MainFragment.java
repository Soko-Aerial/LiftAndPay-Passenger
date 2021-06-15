package com.example.liftandpay_passenger.MainActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;

import com.example.liftandpay_passenger.R;
import com.example.liftandpay_passenger.SearchedRide.SearchedRides;
import com.example.liftandpay_passenger.SettingUp.SignUp003;
import com.example.liftandpay_passenger.fastClass.LongiLati;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.mapboxsdk.Mapbox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MainFragment extends Fragment {


    AppCompatAutoCompleteTextView searchOrigin;
    AppCompatAutoCompleteTextView searchDestination;

    private TextView rideSearchbtn;
    private FloatingActionButton floatbtn;

    View connectorView;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private   String json = null;
    private  int getNum;
    private JSONObject obj001;
    private  JSONObject obj002;
    private JSONObject obj003;
    private JSONObject obj004;
    private JSONArray arr002;


    private LongiLati longiLati;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(Objects.requireNonNull(getContext()), getString(R.string.mapbox_access_token));
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        inflater.inflate(R.layout.fragment_main, container, false);

        floatbtn = v.findViewById(R.id.floatBtn);
        rideSearchbtn = v.findViewById(R.id.ridSearchBtn);
        searchOrigin = v.findViewById(R.id.originSearchId);
        searchDestination = v.findViewById(R.id.destinationSearchId);
        searchDestination.setVisibility(View.GONE);
        connectorView = v.findViewById(R.id.connectorId);
        connectorView.setVisibility(View.GONE);



        rideSearchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchOrigin.getText().toString().equals ("") || searchDestination.getText().toString().equals(""))
                {
                    Toast.makeText(getContext(),"Can't Search",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(getContext(), SearchedRides.class);
                    intent.putExtra("stLoc",searchOrigin.getText().toString());
                    intent.putExtra("endLoc",searchDestination.getText().toString());
                    startActivity(intent);
                }
            }
        });


        floatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mAuth.signOut();
                Intent intent = new Intent(getContext(), SignUp003.class);
                startActivity(intent);
            }
        });


        try {
            InputStream is = getActivity().getAssets().open("overpass.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            obj001 = new JSONObject(json);

            JSONArray arr001 = (JSONArray) obj001.get("features");
            int sd = arr001.length();

            ArrayList<String> arrayList = new ArrayList<String>();
            HashMap<String,LongiLati> busProp = new HashMap<>();

            longiLati = new LongiLati(11,11);
            busProp.put("My Bus", longiLati);

            longiLati = new LongiLati(22,22);
            busProp.put("My Bus", longiLati);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (getNum<sd) {
                        try {
                            obj002 = (JSONObject) arr001.get(getNum);
                            obj003 = (JSONObject) obj002.get("properties");
                            if (obj003.has("name")) {
                                String theBusStopName = (String) obj003.get("name");

                                obj004 = (JSONObject) obj002.get("geometry");
                                arr002 = (JSONArray) obj004.get("coordinates");

                                String lon = (String) arr002.getString(0);
                                String lat = (String) arr002.getString(1);

                                if (!arrayList.contains(theBusStopName))
                                arrayList.add(theBusStopName);

                                busProp.put("my",longiLati);

                                String fSt = "Lon: " + lon + "\n" + "Lat: " + lat + "\n" + "Name: " + arrayList.get(0);

                                double lond = Double.parseDouble(lon);
                                double latd = Double.parseDouble(lat);
                            }

                            getNum = getNum + 1;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    boolean check;

                  getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                makeSuggestion( searchOrigin,arrayList);
                                makeSuggestion(searchDestination,arrayList);
                                                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    });


                }
            }).start();

        } catch (IOException | JSONException ex) {
            Log.e("Error Code: ",ex.getMessage());
            Toast.makeText(getContext(),"Couldn't fetch "+ex.getMessage(),Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
                searchDestination.setVisibility(View.VISIBLE);
                connectorView.setVisibility(View.VISIBLE);

        return v;
    }


    private void makeSuggestion(AutoCompleteTextView autoCompleteTextView, ArrayList<String> arrayList){
        ArrayAdapter adapter = new ArrayAdapter(getContext(),R.layout.model_search_list, arrayList);

        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setDropDownVerticalOffset(10);
        autoCompleteTextView.setVerticalScrollBarEnabled(false);
        autoCompleteTextView.setDropDownHeight(400);
        autoCompleteTextView.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                boolean check = false;
                for (int j = 0; j < arrayList.size();j++)
                {
                    if (arrayList.get(j).contentEquals(text)) {
                        check = true;
                    }
                }

                return check;
            }

            @Override
            public CharSequence fixText(CharSequence invalidText) {
                Toast.makeText(getContext(),"incorrect bus stop",Toast.LENGTH_LONG).show();
                return null;
            }
        });

        autoCompleteTextView.performValidation();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}