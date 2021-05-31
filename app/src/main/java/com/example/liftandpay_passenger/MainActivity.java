package com.example.liftandpay_passenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import hu.supercluster.overpasser.library.output.OutputModificator;
import hu.supercluster.overpasser.library.output.OutputOrder;
import hu.supercluster.overpasser.library.output.OutputVerbosity;
import hu.supercluster.overpasser.library.query.OverpassQuery;
import nl.joery.animatedbottombar.AnimatedBottomBar;

import static hu.supercluster.overpasser.library.output.OutputFormat.JSON;

public class MainActivity extends AppCompatActivity {
    AnimatedBottomBar animatedBottomBar;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animatedBottomBar = findViewById(R.id.animatedBottomBar);
        if(savedInstanceState==null){

            animatedBottomBar.selectTabById(R.id.navigation_home,true);
            fragmentManager = getSupportFragmentManager();
            MainFragment homeFragment = new MainFragment();
            fragmentManager.beginTransaction().replace(R.id.container,homeFragment).commit();

        }
        animatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int lastIndex, @Nullable AnimatedBottomBar.Tab lastTab, int newIndex, @NotNull AnimatedBottomBar.Tab newTab) {
                Fragment fragment = null;
                switch (newTab.getId()){

                    case R.id.navigation_home:
                        fragment = new MainFragment();
                        break;

                    case R.id.navigation_radar:
                        fragment = new RidesFragment();
                        break;

                    case R.id.navigation_payment:
                        fragment = new PayFragment();
                        break;

                    case R.id.navigation_profile:
                        fragment = new ProfileFragment();
                        break;

                }
                if(fragment!=null){
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
                }

            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {

            }
        });


        String query;
        query = new OverpassQuery()
                .format(JSON)
                .timeout(30)
                .filterQuery()
                .node()
                .amenity("parking")
                .tagNot("access", "private")
                .boundingBox(
                        47.48047027491862, 19.039797484874725,
                        47.51331674014172, 19.07404761761427
                )
                .end()
                .output(OutputVerbosity.BODY, OutputModificator.CENTER, OutputOrder.QT, 100)
                .build();

        Toast.makeText(MainActivity.this,query,Toast.LENGTH_LONG).show();

    }

}