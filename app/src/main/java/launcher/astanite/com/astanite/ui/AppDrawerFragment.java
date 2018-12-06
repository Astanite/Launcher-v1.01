package launcher.astanite.com.astanite.ui;


import android.app.Activity;
import android.os.Bundle;
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

    public AppDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appsAdapter = new AppsAdapter(new ArrayList<>(), Glide.with(this), getContext());
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

        mainViewModel
                .currentModeApps
                .observe(this, appInfos -> {
                    appsAdapter.updateAppsList(appInfos);
                });
        mainViewModel
                .getCurrentIntention()
                .observe(this, intention -> {
                    intentionEditText.setText(intention);
                });

        mainViewModel.getCurrentMode()
                .observe(this, mode -> this.currentMode = mode);
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

    public void inflateOutOfModeMenu() {
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

    public void inflateInModeMenu() {
        popupMenu.inflate(R.menu.in_mode_menu);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.exitMode) {
                long currentTime = System.currentTimeMillis();
                long enteredTime = mainViewModel.getTimeOfEnteringMode();
                long delta = currentTime - enteredTime;
                if (delta < mainViewModel.getModeTime() && delta != 0) {
                    timerScreenListener.showTimer();
                } else {
                    mainViewModel.setCurrentMode(Constants.MODE_NONE);
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
