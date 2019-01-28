package launcher.astanite.com.astanite.ui.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.data.MyApplication;
import launcher.astanite.com.astanite.utils.Constants;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private FlaggedAppsFragment flaggedAppsFragment;
    private Bundle argument = new Bundle() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_settings);

        SettingsViewModel settingsViewModel = ViewModelProviders
                .of(this)
                .get(SettingsViewModel.class);

        flaggedAppsFragment = new FlaggedAppsFragment();
        ModesListFragment modesListFragment = new ModesListFragment();

        if(getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getBoolean("settingsinstall",true))
        {
            settingsViewModel.currentFragment.setValue(Constants.FRAGMENT_DISTRACTIVE_APPS);
            settingsViewModel.currentMode.setValue(Constants.DISTRACTIVE_APP);
        } else {
            int tempKey = ((MyApplication) this.getApplication()).getSettingsmode();

            if(tempKey != Constants.MODE_NONE)
            {
                settingsViewModel.currentFragment.postValue(Constants.FRAGMENT_FLAGGED_APPS);
                settingsViewModel.currentMode.setValue(tempKey);
            }
            else if (savedInstanceState == null) {
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
        }



        settingsViewModel.currentFragment
                .observe(this, currentFragment -> {
                    Log.d(TAG, "Current fragment: " + currentFragment);
                    switch (currentFragment) {

                        case Constants.FRAGMENT_FLAGGED_APPS:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                                    .replace(R.id.settingsContainer, flaggedAppsFragment)
                                    .commit();
                            break;
                        case Constants.FRAGMENT_DISTRACTIVE_APPS:
                            argument.putBoolean("isDist", true);
                            flaggedAppsFragment.setArguments(argument);
                            Log.d("isDist", String.valueOf(true));
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                                    .replace(R.id.settingsContainer, flaggedAppsFragment)
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
