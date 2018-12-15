package launcher.astanite.com.astanite.ui.settings;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import launcher.astanite.com.astanite.utils.Constants;
import launcher.astanite.com.astanite.R;

public class ModesListFragment extends Fragment {

    private SettingsViewModel settingsViewModel;

    public ModesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settingsViewModel = ViewModelProviders
                .of(getActivity())
                .get(SettingsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modes_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView focusModeSettings = view.findViewById(R.id.focusModeSettingsTextview),
                sleepModeSettings = view.findViewById(R.id.sleepModeSettingsTextview),
                myModeSettings = view.findViewById(R.id.myModeSettingsTextview) ,
                distractiveAppsSettings = view.findViewById(R.id.distractiveAppsSettings);

        focusModeSettings.setOnClickListener(someView -> {
            settingsViewModel.currentFragment.setValue(Constants.FRAGMENT_SETTINGS);
            settingsViewModel.currentMode.setValue(Constants.MODE_FOCUS);
        });

        sleepModeSettings.setOnClickListener(someView -> {
            settingsViewModel.currentFragment.setValue(Constants.FRAGMENT_SETTINGS);
            settingsViewModel.currentMode.setValue(Constants.MODE_SLEEP);
        });
        myModeSettings.setOnClickListener(someView -> {
            settingsViewModel.currentFragment.setValue(Constants.FRAGMENT_SETTINGS);
            settingsViewModel.currentMode.setValue(Constants.MY_MODE);
        });
        distractiveAppsSettings.setOnClickListener(view1 -> {
            settingsViewModel.currentFragment.setValue(Constants.FRAGMENT_SETTINGS);
            settingsViewModel.currentMode.setValue(Constants.DISTRACTIVE_APP);
        });
    }
}
