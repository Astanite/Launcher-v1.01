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
    private TextView focusModeSettings;
    private TextView workModeSettings;
    private TextView sleepModeSettings;
    private TextView myModeSettings;

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
        focusModeSettings = view.findViewById(R.id.focusModeSettingsTextview);
        workModeSettings = view.findViewById(R.id.workModeSettingsTextview);
        sleepModeSettings = view.findViewById(R.id.sleepModeSettingsTextview);
        myModeSettings = view.findViewById(R.id.myModeSettingsTextview);

        focusModeSettings.setOnClickListener(someView -> {
            settingsViewModel.currentFragment.setValue(Constants.FRAGMENT_SETTINGS);
            settingsViewModel.currentMode.setValue(Constants.MODE_FOCUS);
        });
        workModeSettings.setOnClickListener(someView -> {
            settingsViewModel.currentFragment.setValue(Constants.FRAGMENT_SETTINGS);
            settingsViewModel.currentMode.setValue(Constants.MODE_WORK);
        });
        sleepModeSettings.setOnClickListener(someView -> {
            settingsViewModel.currentFragment.setValue(Constants.FRAGMENT_SETTINGS);
            settingsViewModel.currentMode.setValue(Constants.MODE_SLEEP);
        });
        myModeSettings.setOnClickListener(someView -> {
            settingsViewModel.currentFragment.setValue(Constants.FRAGMENT_SETTINGS);
            settingsViewModel.currentMode.setValue(Constants.MY_MODE);
        });
    }
}
