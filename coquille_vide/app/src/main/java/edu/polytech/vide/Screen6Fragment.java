package edu.polytech.vide;import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.*;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;

public class Screen6Fragment extends Fragment {
    private final String TAG = "frallo " + getClass().getSimpleName();
    private Notifiable notifiable;
    private static final int REQUEST_LOCATION_PERMISSION = 123;

    private MapView mapView;
    private Marker currentLocationMarker;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    public Screen6Fragment() {
        Log.d(TAG, "screenFragment type 6 created");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (requireActivity() instanceof Notifiable) {
            notifiable = (Notifiable) requireActivity();
        } else {
            throw new AssertionError("Classe " + requireActivity().getClass().getName() + " ne met pas en œuvre Notifiable.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(requireContext(), androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()));


        // Configurer osmdroid pour éviter le cache sur la mémoire externe
        File cacheDir = new File(requireContext().getCacheDir(), "osmdroid");
        Configuration.getInstance().setOsmdroidBasePath(cacheDir);
        Configuration.getInstance().setOsmdroidTileCache(new File(cacheDir, "tiles"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen6, container, false);

        mapView = view.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        mapView.getController().setZoom(18.0);
        mapView.getController().setCenter(new GeoPoint(43.6156, 7.0718));

        addStaticMarkers();
        checkLocationPermission();

        return view;
    }

    private void addStaticMarkers() {
        mapView.getOverlays().add(addMarker(new GeoPoint(43.65120, 7.00450), "fred", "home", R.drawable.home));
        mapView.getOverlays().add(addMarker(new GeoPoint(43.6156, 7.0718), "job", "work", R.drawable.work));
        mapView.getOverlays().add(addMarker(new GeoPoint(43.64950, 7.00418), "lisa", "teacher", R.drawable.work));
    }

    private Marker addMarker(GeoPoint point, String title, String desc, int iconRes) {
        Marker marker = new Marker(mapView);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setPosition(point);
        marker.setTitle(title);
        marker.setSubDescription(desc);
        marker.setIcon(ContextCompat.getDrawable(requireContext(), iconRes));
        return marker;
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionNeededDialog();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            }
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // sécurité supplémentaire
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                Location loc = result.getLastLocation();
                if (loc == null) return;

                GeoPoint point = new GeoPoint(loc.getLatitude(), loc.getLongitude());

                if (currentLocationMarker == null) {
                    currentLocationMarker = addMarker(point, "Moi", "Ma position", org.osmdroid.library.R.drawable.person);
                    mapView.getOverlays().add(currentLocationMarker);
                } else {
                    currentLocationMarker.setPosition(point);
                }

                mapView.getController().animateTo(point);
                mapView.invalidate();
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null)
            fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showBlockedPermissionDialog();
                } else {
                    Log.w(TAG, "Permission de localisation refusée");
                }
            }
        }
    }

    private void showPermissionNeededDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Permission requise")
                .setMessage("La localisation est nécessaire pour afficher votre position sur la carte.")
                .setPositiveButton("Autoriser", (dialog, which) ->
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION))
                .setNegativeButton("Annuler", null)
                .setCancelable(false)
                .show();
    }

    private void showBlockedPermissionDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Permission bloquée")
                .setMessage("La permission a été refusée définitivement. Veuillez l'activer manuellement dans les paramètres.")
                .setPositiveButton("Ouvrir les paramètres", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Annuler", null)
                .setCancelable(false)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        checkLocationPermission(); // Si l’utilisateur revient depuis les paramètres
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        stopLocationUpdates();
    }
}
