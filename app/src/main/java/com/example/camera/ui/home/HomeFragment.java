package com.example.camera.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.camera.R;
import com.example.camera.databinding.FragmentHomeBinding;
import com.example.camera.util.SubscribeClient;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SubscribeClient waterHeight1;
    private SubscribeClient foodLast1;
    private SubscribeClient waterTemp1;
    private SubscribeClient grassLight1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView waterHeight=root.findViewById(R.id.water_height);
        TextView foodLast=root.findViewById(R.id.food_last);
        TextView waterTemp=root.findViewById(R.id.water_temp);
        TextView grassLight=root.findViewById(R.id.grass_light);

        MqttCallback mc=new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                if(topic.equals("waterHeight"))
                {
                    waterHeight.setText(new String(message.getPayload())+"cm");
                }
                else if(topic.equals("foodLast"))
                {
                    foodLast.setText(new String(message.getPayload())+"%");
                }
                else if(topic.equals("waterTemp"))
                {
                    waterTemp.setText(new String(message.getPayload())+"℃ ");
                }
                else if(topic.equals("grassLight"))
                {
                    grassLight.setText(new String(message.getPayload()));
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        };
        waterHeight1 = new SubscribeClient("waterHeight", mc);
        foodLast1 = new SubscribeClient("foodLast", mc);
        waterTemp1 = new SubscribeClient("waterTemp", mc);
        grassLight1 = new SubscribeClient("grassLight", mc);

        Switch switchHome=root.findViewById(R.id.switch_home);
        switchHome.setChecked(false);
        switchHome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    waterHeight1.connect();
                    foodLast1.connect();
                    waterTemp1.connect();
                    grassLight1.connect();
                }
                else{
                    waterHeight1.disconnect();
                    foodLast1.disconnect();
                    waterTemp1.disconnect();
                    grassLight1.disconnect();
                }
            }
        });
        Switch swTg=root.findViewById(R.id.switchtg);
        swTg.setChecked(false);
        swTg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    waterHeight1.connect();
                    foodLast1.connect();
                    waterTemp1.connect();
                    grassLight1.connect();
                }
                else{
                    waterHeight1.disconnect();
                    foodLast1.disconnect();
                    waterTemp1.disconnect();
                    grassLight1.disconnect();
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        waterHeight1.disconnect();
        foodLast1.disconnect();
        waterTemp1.disconnect();
        grassLight1.disconnect();
        binding = null;
    }
}