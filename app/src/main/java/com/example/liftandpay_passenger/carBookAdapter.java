package com.example.liftandpay_passenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class carBookAdapter extends RecyclerView.Adapter<carBookAdapter.myViewHolder> {
    ArrayList<carBookingModel> carHolder;
    Context carBookContext;

    public carBookAdapter(ArrayList<carBookingModel> carHolder, Context carBookContext){
        this.carHolder = carHolder;
        this.carBookContext = carBookContext;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_car_item_card,parent, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        holder.bookCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                FragmentManager manager = ((AppCompatActivity)carBookContext).getSupportFragmentManager();
                bottomSheetDialog.show(manager, null);
                Toast.makeText(carBookContext,"Hellow" , Toast.LENGTH_LONG ).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return carHolder.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        MaterialButton bookCarBtn;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.carID);
            bookCarBtn = itemView.findViewById(R.id.book_car);
        }
    }
}
