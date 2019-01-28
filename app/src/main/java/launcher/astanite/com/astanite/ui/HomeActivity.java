package launcher.astanite.com.astanite.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.security.Permission;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.data.MyApplication;
import launcher.astanite.com.astanite.ui.settings.SettingsActivity;
import launcher.astanite.com.astanite.utils.BroadCastReceiver;
import launcher.astanite.com.astanite.utils.Constants;
import launcher.astanite.com.astanite.viewmodel.MainViewModel;

public class HomeActivity extends AppCompatActivity implements
        AppDrawerFragment.SettingsScreenListener,
        HomeScreenFragment.PenaltyScreenListener,
        PenaltyFragment.HomeScreenListener,
        BroadCastReceiver.SendToHomeActivity,
        AppDrawerFragment.TimerScreenListener {

    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = HomeActivity.class.getSimpleName();

    private MainViewModel mainViewModel;
    private HomeScreenFragment homeScreenFragment;
    private AppDrawerFragment appDrawerFragment;
    private DataAnalysisFragment dataAnalysisFragment;
    private FloatingActionButton allAppsButton;
    public ImageView ivDataAnal;
    private CoordinatorLayout rootView;
    private BroadCastReceiver receiver = new BroadCastReceiver(this);
    private boolean AppDrawerStateOpen = false;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    //user not registered. Send it to Login Activity
                    Intent intent = new Intent(HomeActivity.this, IntroActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Log.e("permission time?", "Yess ");

                }
            }
        };

        Log.i("MYINFO", "Creating Home Activity");
        allAppsButton = findViewById(R.id.allAppsButton);
        ivDataAnal = findViewById(R.id.iv_dataAnal);
        rootView = findViewById(R.id.rootView);

        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        registerReceiver(new BroadCastReceiver(this), filter);
        registerReceiver(new BroadCastReceiver(this), filter);

        mainViewModel = ViewModelProviders
                .of(this)
                .get(MainViewModel.class);

        mainViewModel.getResolveInfoList(this);

        homeScreenFragment = new HomeScreenFragment();
        appDrawerFragment = new AppDrawerFragment();
        dataAnalysisFragment = new DataAnalysisFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, homeScreenFragment, "HomeScreenFragment")
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
        }

        allAppsButton.setOnClickListener(someView -> {
            mainViewModel.isAppDrawerOpen.setValue(true);
            openAppDrawer();
        });
        ivDataAnal.setOnClickListener(someView1 -> {
            mainViewModel.isAnalysisOpen.setValue(true);
            openDataAnalysis();
        });

        mainViewModel.getCurrentMode()
                .observe(this, mode -> {
                    String snackbarMessage;
                    switch (mode) {
                        case Constants.MODE_FOCUS:
                            snackbarMessage = "Switched to Focus mode. Notifications Blocked.";
                            Snackbar.make(rootView, snackbarMessage, Snackbar.LENGTH_SHORT).show();
                            break;
                        case Constants.MODE_SLEEP:
                            snackbarMessage = "Switched to Sleep mode. Notifications Blocked.";
                            Snackbar.make(rootView, snackbarMessage, Snackbar.LENGTH_SHORT).show();
                            break;
                        case Constants.MY_MODE:
                            snackbarMessage = "Switched to My Mode. Notifications Blocked.";
                            Snackbar.make(rootView, snackbarMessage, Snackbar.LENGTH_SHORT).show();
                            break;

                    }
                });
    }

    @Override
    public void sendToHomeActivity() {
        refreshAllData();
    }

    public void refreshAllData() {
        mainViewModel.getResolveInfoList(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(AppDrawerStateOpen == true)
            getMenuInflater().inflate(R.menu.options_on_long_press_app_drawer, menu);
        else
            getMenuInflater().inflate(R.menu.options_on_long_press_home_screen, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String packageName = prefs.getString("packageName", "Default Package Name");
        int homeScreenApps;
        switch (item.getItemId()) {
            case R.id.uninstall:
                Intent intent1 = new Intent(Intent.ACTION_DELETE);
                intent1.setData(Uri.parse("package:" + packageName));
                startActivity(intent1);
                closeAppDrawer();
                return true;
            case R.id.addToHomeScreen:
                homeScreenApps = prefs.getInt("homeScreenApps", -1);
                SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit();
                switch (homeScreenApps) {
                    case 1:
                        editor.putString("HomeApp2", packageName);
                        editor.putInt("homeScreenApps", 2);
                        editor.putString("Ha2label", packtoapp(packageName));
                        editor.apply();
                        try {
                            Toast.makeText(this, "Added " + getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packageName, 0)), Toast.LENGTH_SHORT).show();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        editor.putString("HomeApp3", packageName);
                        editor.putInt("homeScreenApps", 3);
                        editor.putString("Ha3label", packtoapp(packageName));
                        editor.apply();
                        try {
                            Toast.makeText(this, "Added " + getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packageName, 0)), Toast.LENGTH_SHORT).show();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        editor.putString("HomeApp4", packageName);
                        editor.putInt("homeScreenApps", 4);
                        editor.putString("Ha4label", packtoapp(packageName));
                        editor.apply();
                        try {
                            Toast.makeText(this, "Added " + getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packageName, 0)), Toast.LENGTH_SHORT).show();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        Toast.makeText(this, "Homescreen is full", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        editor.putString("HomeApp1", packageName);
                        editor.putInt("homeScreenApps", 1);
                        editor.putString("Ha1label", packtoapp(packageName));
                        editor.apply();
                        try {
                            Toast.makeText(this, "Added " + getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packageName, 0)), Toast.LENGTH_SHORT).show();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return true;
            case R.id.appInfo:
                Intent intent2 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent2.setData(Uri.parse("package:" + packageName));
                startActivity(intent2);
                return true;
            case R.id.removeFromHomeScreen:
                int homeScreenApps2 = prefs.getInt("homeScreenApps",-1);
                SharedPreferences.Editor editor2 = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit();
                editor2.putInt("homeScreenApps",homeScreenApps2 - 1);
                editor2.putString("removedPackageName",packageName);
                editor2.putString("removedLabel", packtoapp(packageName));
                editor2.apply();

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof HomeScreenFragment) {
                    FragmentTransaction fragTransaction =   getSupportFragmentManager().beginTransaction();
                    fragTransaction.detach(currentFragment);
                    fragTransaction.attach(currentFragment);
                    fragTransaction.commit();}
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void openAppDrawer() {
        AppDrawerStateOpen = true;
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_bottom, R.anim.fade_out)
                .replace(R.id.fragment_container, appDrawerFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
        allAppsButton.hide();
        ivDataAnal.setVisibility(View.GONE);

    }

    public void closeAppDrawer() {
        AppDrawerStateOpen = false;
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.exit_to_bottom, R.anim.fade_out)
                .replace(R.id.fragment_container, homeScreenFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
        allAppsButton.show();
        ivDataAnal.setVisibility(View.VISIBLE);

    }
    private void openDataAnalysis() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_left, R.anim.fast_fade_out)
                .replace(R.id.fragment_container, dataAnalysisFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
        allAppsButton.hide();
        ivDataAnal.setVisibility(View.GONE);
    }

    private void closeDataAnalysis() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.exit_to_left)
                .replace(R.id.fragment_container, homeScreenFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
        allAppsButton.show();
        ivDataAnal.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (mainViewModel.isAppDrawerOpen.getValue()) {
            closeAppDrawer();
            mainViewModel.isAppDrawerOpen.setValue(false);
        } else if (mainViewModel.isPenaltyScreenOpen) {
            showHomeScreen();
            mainViewModel.isPenaltyScreenOpen = false;
        }
        if (mainViewModel.isAnalysisOpen.getValue()){
            closeDataAnalysis();
            mainViewModel.isAnalysisOpen.setValue(false);
        }
        if(mainViewModel.isTimerOpen)
        {
            showHomeScreen();
            mainViewModel.isTimerOpen = false;
        }
    }


    @Override
    public void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void showPenaltyScreen(int mode) {
        mainViewModel.penaltyScreenTriggeredForMode.setValue(mode);
        PenaltyFragment penaltyFragment = new PenaltyFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragment_container, penaltyFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
        mainViewModel.isPenaltyScreenOpen = true;
        allAppsButton.hide();
        ivDataAnal.setVisibility(View.GONE);
    }

    @Override
    public void showHomeScreen() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.exit_to_bottom)
                .replace(R.id.fragment_container, homeScreenFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
        allAppsButton.show();
        ivDataAnal.setVisibility(View.VISIBLE);

    }

    @Override
    public void showTimer() {
        TimerFragment timerFragment = new TimerFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                .replace(R.id.fragment_container, timerFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
        mainViewModel.isTimerOpen = true;
        allAppsButton.hide();
        ivDataAnal.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mainViewModel.isAppDrawerOpen.getValue()) {
            closeAppDrawer();
            mainViewModel.isAppDrawerOpen.setValue(false);
        } else if (mainViewModel.isPenaltyScreenOpen) {
            if(((MyApplication)getApplication()).getPenaltymode() == false)
            {
                showHomeScreen();
                mainViewModel.isPenaltyScreenOpen = false;
            }
            else
            {
                ((MyApplication)getApplication()).setPenaltymode(false);
            }
        }
        if (mainViewModel.isAnalysisOpen.getValue()){
            showHomeScreen();
            mainViewModel.isAnalysisOpen.setValue(false);
        }
        if(mainViewModel.isTimerOpen)
        {
            showHomeScreen();
            mainViewModel.isTimerOpen = false;
        }
    }

    public String packtoapp(String pname) {
        PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo( pname, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        return applicationName;
    }
}
