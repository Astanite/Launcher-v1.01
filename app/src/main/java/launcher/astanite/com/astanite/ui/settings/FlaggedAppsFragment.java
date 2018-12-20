package launcher.astanite.com.astanite.ui.settings;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.data.AppInfo;
import launcher.astanite.com.astanite.utils.Constants;

public class FlaggedAppsFragment extends Fragment {

    private static final String TAG = FlaggedAppsFragment.class.getSimpleName();
    private SettingsViewModel settingsViewModel;
    private FlaggedAppsAdapter flaggedAppsAdapter;
    private View rootView;
    private FloatingActionButton fab_save;

    public FlaggedAppsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settingsViewModel = ViewModelProviders
                .of(getActivity())
                .get(SettingsViewModel.class);
        settingsViewModel.getAllApps()
                .observe(this, newList -> {
                    flaggedAppsAdapter.updateList(newList);
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flagged_apps, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        RecyclerView flaggedAppsRecyclerview = view.findViewById(R.id.flaggedAppsRecyclerview);
        CardView cv_focus = view.findViewById(R.id.cv_focus_mode),
                cv_sleep = view.findViewById(R.id.cv_sleep_mode),
                cv_leisure = view.findViewById(R.id.cv_leisure_mode);
        fab_save = view.findViewById(R.id.fab_save);

        flaggedAppsAdapter = new FlaggedAppsAdapter(new ArrayList<>(), Glide.with(getContext()));
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        flaggedAppsRecyclerview.setAdapter(flaggedAppsAdapter);
        flaggedAppsRecyclerview.setLayoutManager(layoutManager);

        cv_focus.setOnClickListener(view1 -> {
            Log.d("cv_focus_clicked", String.valueOf(Constants.MODE_FOCUS));
            settingsViewModel.currentFragment.setValue(Constants.FRAGMENT_FLAGGED_APPS);
            settingsViewModel.currentMode.setValue(Constants.MODE_FOCUS);
        });
        cv_leisure.setOnClickListener(view12 -> {
            Log.d("cv_leisure_clicked", String.valueOf(Constants.MY_MODE));
            settingsViewModel.currentFragment.setValue(Constants.FRAGMENT_FLAGGED_APPS);
            settingsViewModel.currentMode.setValue(Constants.MY_MODE);
        });
        cv_sleep.setOnClickListener(view13 -> {
            Log.d("cv_sleep_clicked", String.valueOf(Constants.MODE_SLEEP));
            settingsViewModel.currentFragment.setValue(Constants.FRAGMENT_FLAGGED_APPS);
            settingsViewModel.currentMode.setValue(Constants.MODE_SLEEP);
        });

        flaggedAppsRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab_save.isShown()) {
                    fab_save.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab_save.show();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        fab_save.setOnClickListener(view1 -> {
            if (flaggedAppsAdapter.getCheckedApps().size() != 0) {
                Log.d(TAG, "Save menu option clicked");
                List<AppInfo> flaggedApps = flaggedAppsAdapter.getCheckedApps();
                settingsViewModel.saveFlaggedApps(flaggedApps);
                Snackbar.make(rootView, "Flagged Apps updated!", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(rootView, "Select at least One App!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
