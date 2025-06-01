package edu.polytech.vide;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.content.Context;

public class Screen5Fragment extends Fragment {

    private final String TAG = "frallo " + getClass().getSimpleName();
    private Notifiable notifiable;
    private ImageView imageView;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    public Screen5Fragment() {
        Log.d(TAG, "Screen5Fragment created");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (requireActivity() instanceof Notifiable) {
            notifiable = (Notifiable) requireActivity();
            Log.d(TAG, "Class " + requireActivity().getClass().getSimpleName() + " implements Notifiable.");
        } else {
            throw new AssertionError("Classe " + requireActivity().getClass().getName() + " ne met pas en œuvre Notifiable.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lancer la caméra et récupérer l'image
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        Bitmap picture = (Bitmap) result.getData().getExtras().get("data");
                        imageView.setImageBitmap(picture);

                        // Notifier l'activité que l'image a été prise
                        notifiable.onDataChange(Notifiable.TAG_MENU5, "Photo captured");
                    }
                }
        );

        // Gestion de la permission
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        takePicture();
                    } else {
                        explain();
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen5, container, false);

        imageView = view.findViewById(R.id.imageView);
        Button buttonTakePhoto = view.findViewById(R.id.button);

        buttonTakePhoto.setOnClickListener(v -> {
            // Notifier le clic sur le bouton
            notifiable.onClick(Notifiable.TAG_MENU5);

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            } else {
                takePicture();
            }
        });

        return view;
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        }
    }

    private void explain() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Permission requise")
                    .setMessage("L'accès à la caméra est nécessaire pour prendre une photo.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        } else {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Permission refusée")
                    .setMessage("La permission a été refusée définitivement. Allez dans les paramètres.")
                    .setPositiveButton("Ouvrir les paramètres", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        }
    }
}