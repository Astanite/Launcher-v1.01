package launcher.astanite.com.astanite.ui;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.data.AppInfo;
import launcher.astanite.com.astanite.data.MyApplication;
import launcher.astanite.com.astanite.utils.Constants;
import launcher.astanite.com.astanite.viewmodel.MainViewModel;

public class AppDrawerFragment extends Fragment implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        sharedPreferences.edit()
                .putString(Constants.KEY_INTENTION, intentionEditText.getText().toString())
                .apply();
        Log.d("upd Home", sharedPreferences.getString(Constants.KEY_INTENTION, ""));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    interface SettingsScreenListener {
        void showSettings();
    }

    interface TimerScreenListener {
        void showTimer();
    }

    private static final String TAG = AppDrawerFragment.class.getSimpleName();

    private MainViewModel mainViewModel;
    private RecyclerView appsRecyclerView;
    private AppsAdapter appsAdapter;
    private LinearLayoutManager layoutManager;
    private EditText intentionEditText;
    private int currentMode;
    private View root;
    private SettingsScreenListener settingsScreenListener;
    private TimerScreenListener timerScreenListener;
    private SharedPreferences sharedPreferences;
    private ImageView[] icons ;
    private TextView[] names ;

    public AppDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appsAdapter = new AppsAdapter(new ArrayList<>(), Glide.with(this), getContext());
        sharedPreferences = this.getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!(getActivity() instanceof SettingsScreenListener) || !(getActivity() instanceof TimerScreenListener))
            throw new ClassCastException();
        else {
            this.settingsScreenListener = (SettingsScreenListener) getActivity();
            this.timerScreenListener = (TimerScreenListener) getActivity();
        }

        mainViewModel = ViewModelProviders
                .of(getActivity())
                .get(MainViewModel.class);



        mainViewModel.getCurrentMode()
                .observe(this, mode -> this.currentMode = mode);
        Log.d("current_Mode", String.valueOf(currentMode));

        mainViewModel
                .currentModeApps
                .observe(this, appInfos -> {
                    appsAdapter.updateAppsList(appInfos, currentMode);
                });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_drawer, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = view;
        appsRecyclerView = view.findViewById(R.id.appsRecyclerView);
        intentionEditText = view.findViewById(R.id.intentionEditText);
        intentionEditText.setText("");
        icons = new ImageView[4];
        names = new TextView[4];
        icons[0] = view.findViewById(R.id.icon1);
        icons[1] = view.findViewById(R.id.icon2);
        icons[2] = view.findViewById(R.id.icon3);
        icons[3] = view.findViewById(R.id.icon4);
        names[0] = view.findViewById(R.id.name1);
        names[1] = view.findViewById(R.id.name2);
        names[2] = view.findViewById(R.id.name3);
        names[3] = view.findViewById(R.id.name4);
        setFlaggedApps(); // primary apps of users.

        appsRecyclerView.setAdapter(appsAdapter);
        layoutManager = new GridLayoutManager(getContext(), 4);
        appsRecyclerView.setLayoutManager(layoutManager);

        intentionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    intentionEditText.setHint("");
                else
                    intentionEditText.setHint(R.string.default_intention);
            }
        });

        intentionEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                if(event.getRawX() >= (intentionEditText.getRight() - intentionEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    ((MyApplication) getActivity().getApplication()).setSettingsmode(Constants.MODE_NONE);
                    settingsScreenListener.showSettings();
                    return true;
                }
            }
            return false;
        });
    }

    private void setFlaggedApps() {
        for (int i=0;i<4;i++){
            try {
            icons[i].setImageDrawable(getContext().getPackageManager().getApplicationIcon("com.whatsapp"));
            names[i].setText((String) getContext().getApplicationContext()
                        .getPackageManager()
                        .getApplicationLabel(getContext().getPackageManager()
                                .getApplicationInfo("com.whatsapp", PackageManager.GET_META_DATA)));
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, "icon not found");
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        intentionEditText.setText(sharedPreferences.getString(Constants.KEY_INTENTION, ""));
        Log.d("update intention", intentionEditText.getText().toString());
        intentionEditText.setEnabled(true);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        intentionEditText.setEnabled(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        intentionEditText.addTextChangedListener(this);
    }
}
