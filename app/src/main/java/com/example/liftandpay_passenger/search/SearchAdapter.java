package com.example.liftandpay_passenger.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_passenger.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.viewHolder>{

    private Activity activity;
    private Context context;
    private ArrayList<searchItemModel> searchItemModels;
    private int pos;

    SearchAdapter(Activity activity,Context context,ArrayList<searchItemModel> searchItemModels){
        this.activity = activity;
        this.context = context;
        this.searchItemModels = searchItemModels;
    }

    @NonNull
    @NotNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_search_item, parent, false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.viewHolder holder, @SuppressLint("RecyclerView") int position) {

    holder.theName.setText(searchItemModels.get(position).getTheName());
        holder.theAddress.setText(searchItemModels.get(position).getTheAddress());
        holder.lat = searchItemModels.get(position).getLat();
        holder.lon = searchItemModels.get(position).getLon();
        pos =position;


        holder.itemView.setOnClickListener(View -> {

                Intent i = new Intent();
                i.putExtra("theLocationName",searchItemModels.get(pos).getTheName());
                i.putExtra("theLocationAddress",searchItemModels.get(pos).getTheAddress());
                i.putExtra("theLat",searchItemModels.get(pos).getLat());
                i.putExtra("theLon",searchItemModels.get(pos).getLon());

                activity.setResult(Activity.RESULT_OK,i);
                activity.finish();


        });

    }



    @Override
    public int getItemViewType(int position) {

        return 1;
    }

    @Override
    public int getItemCount() {
        return searchItemModels.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        private TextView theName, theAddress;
        private  double lon, lat;
        public viewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            theName = itemView.findViewById(R.id.theNameId);
            theAddress = itemView.findViewById(R.id.theAddress);
        }
    }
}
