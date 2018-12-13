package launcher.astanite.com.astanite.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;

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
import launcher.astanite.com.astanite.utils.Constants;
import launcher.astanite.com.astanite.viewmodel.MainViewModel;

public class AppDrawerFragment extends Fragment {

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
    private ImageButton appDrawerOptionsButton;
    private PopupMenu popupMenu;
    private int currentMode;
    private View root;
    private SettingsScreenListener settingsScreenListener;
    private TimerScreenListener timerScreenListener;
    private SharedPreferences sharedPreferences;
    private int modeForPenaltyScreen;

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

        Pair<List<AppInfo>, DiffUtil.DiffResult> seedPair = new Pair<>(Collections.emptyList(), null);

        if (!(getActivity() instanceof SettingsScreenListener) || !(getActivity() instanceof TimerScreenListener))
            throw new ClassCastException();
        else {
            this.settingsScreenListener = (SettingsScreenListener) getActivity();
            this.timerScreenListener = (TimerScreenListener) getActivity();
        }

        mainViewModel = ViewModelProviders
                .of(getActivity())
                .get(MainViewModel.class);

        //Setting up intention in App drawer from MainViewModel
        intentionEditText.setText(sharedPreferences.getString(Constants.KEY_INTENTION, ""));

        mainViewModel.getCurrentMode()
                .observe(this, mode -> this.currentMode = mode);
        Log.d("current_Mode", String.valueOf(currentMode));

        mainViewModel
                .currentModeApps
                .observe(this, appInfos -> {
                    for (AppInfo app : appInfos)
                        Log.d("App_Infos", app.packageName);

                    appsAdapter.updateAppsList(appInfos);
                });

        //updating the intention on text change
        // in editText
        intentionEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                //updating intention in SharedPreference directly
                sharedPreferences.edit()
                        .putString(Constants.KEY_INTENTION, intentionEditText.getText().toString())
                        .apply();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_drawer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = view;
        appsRecyclerView = view.findViewById(R.id.appsRecyclerView);
        intentionEditText = view.findViewById(R.id.intentionEdittext);
        appDrawerOptionsButton = view.findViewById(R.id.appDrawerOptionsButton);

        appsRecyclerView.setAdapter(appsAdapter);
        layoutManager = new GridLayoutManager(getContext(), 4);
        appsRecyclerView.setLayoutManager(layoutManager);
        popupMenu = new PopupMenu(getContext(), appDrawerOptionsButton);

        appDrawerOptionsButton.setOnClickListener(someView -> {
            popupMenu = new PopupMenu(getContext(), appDrawerOptionsButton);
            if (currentMode == Constants.MODE_NONE) {
                inflateOutOfModeMenu();
            } else {
                inflateInModeMenu();
            }
            popupMenu.show();
        });
    }

    private void inflateOutOfModeMenu() {
        popupMenu.inflate(R.menu.out_of_mode_menu);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.settings:
                    settingsScreenListener.showSettings();
                    return true;
                default:
                    return true;
            }
        });
    }

    // inflating the menu options (settings in app drawer)
    private void inflateInModeMenu() {
        popupMenu.inflate(R.menu.in_mode_menu);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.exitMode) {
                //if user tries to exit current active mode
                long currentTime = System.currentTimeMillis();
                long enteredTime = mainViewModel.getTimeOfEnteringMode();
                long delta = currentTime - enteredTime;
                if (delta < mainViewModel.getModeTime() && delta != 0) {
                    timerScreenListener.showTimer();
                } else {
                    getContext().stopService(new Intent(getContext(), BlockingAppService.class));
                    mainViewModel.setCurrentMode(Constants.MODE_NONE);
                    Log.d("Service ", "Stopped 2");
                    Snackbar.make(root, "Exited mode", Snackbar.LENGTH_SHORT).show();
                }
            }
            return true;
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
    }
}
