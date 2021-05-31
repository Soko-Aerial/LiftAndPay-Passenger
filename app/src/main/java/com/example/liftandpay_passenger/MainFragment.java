package com.example.liftandpay_passenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.search.SearchEngine;
import com.mapbox.search.SearchRequestTask;
import com.mapbox.search.ui.view.SearchBottomSheetView;

import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MainFragment extends Fragment {


    SearchView searchOrigin;
    SearchView searchDestination;

    View connectorView;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();



    SearchBottomSheetView searchBottomSheetView;
    private SearchEngine searchEngine;
    private SearchRequestTask searchRequestTask;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(Objects.requireNonNull(getContext()), getString(R.string.mapbox_access_token));
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        inflater.inflate(R.layout.fragment_main, container, false);


     /*   mAuth.signInWithEmailAndPassword("hubamp@gmail.com", "123456")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getContext(), "Successfully Signed in",
                                Toast.LENGTH_SHORT).show();
                    }
                });*/


        searchOrigin = v.findViewById(R.id.originSearchId);
        searchDestination = v.findViewById(R.id.destinationSearchId);
        searchDestination.setVisibility(View.GONE);
        connectorView = v.findViewById(R.id.connectorId);
        connectorView.setVisibility(View.GONE);



        searchOrigin.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchDestination.setVisibility(View.VISIBLE);
                connectorView.setVisibility(View.VISIBLE);
                searchDestination.requestFocus(1);
                searchOrigin.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.isEmpty())
                {
                    searchDestination.setVisibility(View.GONE);
                    connectorView.setVisibility(View.GONE);


                }
                return false;
            }
        });


        searchDestination.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getContext(), SearchedRides.class);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return v;
    }


    public static String getNodesViaOverpass(String query) throws IOException, ParserConfigurationException, SAXException {
        String hostname = "http://overpass-turbo.eu";

        URL osm = new URL(hostname);
        HttpURLConnection connection = (HttpURLConnection) osm.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
        printout.writeBytes("data=" + URLEncoder.encode(query, "utf-8"));
        printout.flush();
        printout.close();



        return osm.openConnection().getContentType();

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