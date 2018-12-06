package launcher.astanite.com.astanite.ui.settings;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.data.AppInfo;

public class FlaggedAppsFragment extends Fragment {

    private static final String TAG = FlaggedAppsFragment.class.getSimpleName();

    private SettingsViewModel settingsViewModel;
    private FlaggedAppsAdapter flaggedAppsAdapter;
    private GridLayoutManager layoutManager;
    private RecyclerView flaggedAppsRecyclerview;
    private View rootView;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.flagged_apps_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveFlaggedApps:
                Log.d(TAG, "Save menu option clicked");
                List<AppInfo> flaggedApps = flaggedAppsAdapter.getCheckedApps();
                settingsViewModel.saveFlaggedApps(flaggedApps);
                Snackbar.make(rootView, "Flagged Apps updated!", Snackbar.LENGTH_SHORT).show();
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        flaggedAppsRecyclerview = view.findViewById(R.id.flaggedAppsRecyclerview);
        flaggedAppsAdapter = new FlaggedAppsAdapter(new ArrayList<>(), Glide.with(getContext()));
        layoutManager = new GridLayoutManager(getContext(), 4);
        flaggedAppsRecyclerview.setAdapter(flaggedAppsAdapter);
        flaggedAppsRecyclerview.setLayoutManager(layoutManager);
    }
}
