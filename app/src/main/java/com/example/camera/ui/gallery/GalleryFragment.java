package com.example.camera.ui.gallery;
import com.example.camera.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.camera.databinding.FragmentGalleryBinding;
import com.example.camera.util.PublicClient;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private PublicClient publicClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Switch waterCircle=root.findViewById(R.id.water_circle_switch);
        Switch grassLight=root.findViewById(R.id.grass_light_switch);
        Button foodButton=root.findViewById(R.id.food_button);
        publicClient = new PublicClient();
        waterCircle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    publicClient.sendMessage("1","waterCircleCtrl");
                }else {
                    publicClient.sendMessage("0","waterCircleCtrl");
                }
            }
        });
        grassLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    publicClient.sendMessage("1","grassLightCtrl");
                }else {
                    publicClient.sendMessage("0","grassLightCtrl");
                }
            }
        });

        foodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicClient.sendMessage("1","foodCtrl");
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}