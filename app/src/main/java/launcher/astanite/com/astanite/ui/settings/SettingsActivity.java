package launcher.astanite.com.astanite.ui.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import launcher.astanite.com.astanite.utils.Constants;
import launcher.astanite.com.astanite.R;

import android.os.Bundle;
import android.util.Log;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private SettingsViewModel settingsViewModel;
    private ModesListFragment modesListFragment;
    private FlaggedAppsFragment flaggedAppsFragment;
    private FlaggedContactsFragment flaggedContactsFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        Toolbar toolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);

        settingsViewModel = ViewModelProviders
                .of(this)
                .get(SettingsViewModel.class);

        flaggedAppsFragment = new FlaggedAppsFragment();
        flaggedContactsFragment = new FlaggedContactsFragment();
        settingsFragment = new SettingsFragment();
        modesListFragment = new ModesListFragment();

        if (savedInstanceState == null) {
            // 0 for ModesList Fragment
            // 1 for Settings Fragment
            // 2 for FlaggedAppsFragment
            // 3 for FlaggedContactsFragment
            settingsViewModel.currentFragment.setValue(Constants.FRAGMENT_MODES);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settingsContainer, modesListFragment)
                    .commit();
        }

        settingsViewModel.currentFragment
                .observe(this, currentFragment -> {
                    Log.d(TAG, "Current fragment: " + currentFragment);
                    switch (currentFragment) {
                        case Constants.FRAGMENT_SETTINGS:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.settingsContainer, settingsFragment)
                                    .addToBackStack(null)
                                    .commit();
                            break;
                        case Constants.FRAGMENT_FLAGGED_APPS:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                                    .replace(R.id.settingsContainer, flaggedAppsFragment)
                                    .addToBackStack(null)
                                    .commit();
                            break;
                        case Constants.FRAGMENT_FLAGGED_CONTACTS:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                                    .replace(R.id.settingsContainer, flaggedContactsFragment)
                                    .addToBackStack(null)
                                    .commit();
                            break;
                        default:
                            Log.d(TAG, "Error. Requested fragment: " + currentFragment);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
    }
}
