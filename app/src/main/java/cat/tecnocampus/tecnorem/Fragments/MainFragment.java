package cat.tecnocampus.tecnorem.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import cat.tecnocampus.tecnorem.R;

public class MainFragment extends Fragment {

    private NumberPicker npRowSpeed;
    private TextView txtRowSpeedSelector;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_main, container, false);

        npRowSpeed = v.findViewById(R.id.npRowSpeed);
        txtRowSpeedSelector = v.findViewById(R.id.txtRowSpeedSelector);


        npRowSpeed.setMinValue(10);
        npRowSpeed.setMaxValue(60);
        npRowSpeed.setWrapSelectorWheel(false);

        npRowSpeed.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                txtRowSpeedSelector.setText("Selected Speed: " + newVal);
            }
        });

        return v;
    }
}
