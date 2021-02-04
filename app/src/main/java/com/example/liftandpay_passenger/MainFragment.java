package com.example.liftandpay_passenger;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    MapView mapView;
    private MapView m_mapView;
    private int MAP_DEFAULT_ZOOM = 16;
    private double MAP_DEFAULT_LATITUDE =  33.6667;
    // Default map Longitude:
    private double MAP_DEFAULT_LONGITUDE = 73.1667;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_main, container, false);


        m_mapView = (MapView) v.findViewById(R.id.m_mapview);
        // m_mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        m_mapView.setTileSource(TileSourceFactory.MAPNIK);
        m_mapView.setBuiltInZoomControls(true);
        m_mapView.setMultiTouchControls(true);
        m_mapView.setClickable(true);
        m_mapView.setUseDataConnection(true);
        m_mapView.getController().setZoom(16);
        m_mapView.getController().setCenter(new GeoPoint(33.6667, 73.1667));



        return  v;
    }
}