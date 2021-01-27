package com.example.liftandpay_passenger;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class RidesFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<completedRidesModel> dataholder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_rides, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dataholder = new ArrayList<>();

        completedRidesModel ob1 = new completedRidesModel(R.drawable.img_map,"Toyota Auris","08.09.2018 9:59 PM","20km","$3.00");
        dataholder.add(ob1);
        completedRidesModel ob2 = new completedRidesModel( R.drawable.img_map,"Kia Creed","08.09.2018 9:59 PM","30km","$5.00");
        dataholder.add(ob2);

        recyclerView.setAdapter(new completedRidesAdapter(dataholder));

        return view;
    }
}