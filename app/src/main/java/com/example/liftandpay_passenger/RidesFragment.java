package com.example.liftandpay_passenger;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class RidesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    RecyclerView recView;
    ArrayList<datamodel> dataholder;


    public RidesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment dataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RidesFragment newInstance(String param1, String param2) {
        RidesFragment fragment = new RidesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_rides, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dataholder = new ArrayList<>();
        datamodel ob1 = new datamodel(R.drawable.img_map,"Toyota Auris","08.09.2018 9:59 PM","20km","$3.00");
        dataholder.add(ob1);

        datamodel ob2 = new datamodel( R.drawable.img_map,"Kia Creed","08.09.2018 9:59 PM","30km","$5.00");
        dataholder.add(ob2);

        datamodel ob3 = new datamodel( R.drawable.img_map,"Ford Fiesta","08.09.2018 9:59 PM","30km","$5.00");
        dataholder.add(ob3);

        datamodel ob4 = new datamodel( R.drawable.img_map,"Ford Fiesta","08.09.2018 9:59 PM","30km","$5.00");
        dataholder.add(ob4);

        datamodel ob5 = new datamodel( R.drawable.img_map,"Ford Fiesta","08.09.2018 9:59 PM","30km","$5.00");
        dataholder.add(ob5);

        recyclerView.setAdapter(new myadapter(dataholder));

        datamodel ob6 = new datamodel( R.drawable.img_map,"Ford","08.09.2018 9:59 PM","30km","$5.00");
        dataholder.add(ob6);

        recyclerView.setAdapter(new myadapter(dataholder));

        datamodel ob7 = new datamodel( R.drawable.img_map,"Audi","08.09.2018 9:59 PM","30km","$5.00");
        dataholder.add(ob7);



        recyclerView.setAdapter(new myadapter(dataholder));

        /*
        recView = view.findViewById(R.id.recview);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
*/

        return view;
    }
}