package launcher.astanite.com.astanite.ui.settings;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.ui.HomeActivity;
import launcher.astanite.com.astanite.ui.HomeScreenFragment;

public class ContactusFragment extends Fragment {

    private TextInputEditText et_name, et_email, et_message;
    private Button bt_send;
    private String name, email, message;
    private ProgressBar pb;
    private HomeScreenFragment homeScreenFragment;
    private DatabaseReference databaseRef;

    public ContactusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contactus, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        et_name = view.findViewById(R.id.et_name);
        et_email = view.findViewById(R.id.et_email);
        et_message = view.findViewById(R.id.et_message);
        bt_send = view.findViewById(R.id.bt_send);
        pb = view.findViewById(R.id.pb);

        homeScreenFragment = new HomeScreenFragment();

        databaseRef = FirebaseDatabase.getInstance().getReference("UserMessages");
        bt_send.setOnClickListener(view1 -> {
            name = et_name.getText().toString().trim();
            email = et_email.getText().toString().trim();
            message = et_message.getText().toString().trim();
            if (name.equals("")) {
                Toast.makeText(getContext(), "Enter your name", Toast.LENGTH_SHORT).show();
            } else if (email.equals("")) {
                Toast.makeText(getContext(), "Enter your Phone Number", Toast.LENGTH_SHORT).show();
            } else if (message.equals("")) {
                Toast.makeText(getContext(), "Enter your message", Toast.LENGTH_SHORT).show();
            } else {
                bt_send.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
                //send to message to fireBase
                String id = databaseRef.push().getKey();
                UserMessages userMessages = new UserMessages(name, email, message, getDeviceName(), Build.MANUFACTURER, Build.VERSION.RELEASE, Build.VERSION.SDK_INT);
                databaseRef.child(id).setValue(userMessages);
                Toast.makeText(getContext(), "Thanks for contacting us", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), HomeActivity.class));
            }
        });
        pb.setVisibility(View.GONE);

    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}