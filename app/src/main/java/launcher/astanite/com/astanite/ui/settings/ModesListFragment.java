package launcher.astanite.com.astanite.ui.settings;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.utils.Constants;

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
        RelativeLayout appSelection = view.findViewById(R.id.app_selection),
                distSelection = view.findViewById(R.id.distractive_apps),
                feedback = view.findViewById(R.id.feedback),
                contactus = view.findViewById(R.id.contact_us);


        appSelection.setOnClickListener(view0 ->{
            settingsViewModel.currentFragment.postValue(Constants.FRAGMENT_FLAGGED_APPS);
            settingsViewModel.currentMode.setValue(Constants.MODE_NONE);
        });
        distSelection.setOnClickListener(view1 -> {
            settingsViewModel.currentFragment.setValue(Constants.FRAGMENT_FLAGGED_APPS);
            settingsViewModel.currentMode.setValue(Constants.DISTRACTIVE_APP);
        });
    }
}
