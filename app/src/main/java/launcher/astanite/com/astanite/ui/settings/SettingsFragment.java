package launcher.astanite.com.astanite.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import launcher.astanite.com.astanite.utils.Constants;
import launcher.astanite.com.astanite.R;


public class SettingsFragment extends Fragment {

    private TextView editFlaggedApps;
    private SettingsViewModel settingsViewModel;

    public SettingsFragment() {
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editFlaggedApps = view.findViewById(R.id.flaggedAppsTextview);
        editFlaggedApps.setOnClickListener(someView -> settingsViewModel.currentFragment.postValue(Constants.FRAGMENT_FLAGGED_APPS));
    }
}
