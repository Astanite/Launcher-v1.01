package launcher.astanite.com.astanite.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProviders;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.ui.settings.SettingsActivity;
import launcher.astanite.com.astanite.utils.BroadCastReceiver;
import launcher.astanite.com.astanite.utils.Constants;
import launcher.astanite.com.astanite.viewmodel.MainViewModel;

public class HomeActivity extends AppCompatActivity implements
        AppDrawerFragment.SettingsScreenListener,
        HomeScreenFragment.PenaltyScreenListener,
        PenaltyFragment.HomeScreenListener,
        BroadCastReceiver.SendToMainActivity,
        AppDrawerFragment.TimerScreenListener {

    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = HomeActivity.class.getSimpleName();

    private MainViewModel mainViewModel;
    private HomeScreenFragment homeScreenFragment;
    private AppDrawerFragment appDrawerFragment;
    private FloatingActionButton allAppsButton;
    private CoordinatorLayout rootView;

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
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };

        Log.i("MYINFO", "Creating Home Activity");
        allAppsButton = findViewById(R.id.allAppsButton);
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

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, homeScreenFragment, "HomeScreenFragment")
                    .commit();
        }

        allAppsButton.setOnClickListener(someView -> {
            mainViewModel.isAppDrawerOpen.setValue(true);
            openAppDrawer();
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
    public void sendToMainActivity() {
        refreshAllData();
    }

    public void refreshAllData() {
        closeAppDrawer();
        mainViewModel.getResolveInfoList(this);

        homeScreenFragment = new HomeScreenFragment();
        appDrawerFragment = new AppDrawerFragment();


        allAppsButton.setOnClickListener(someView -> {
            mainViewModel.isAppDrawerOpen.setValue(true);
            openAppDrawer();
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.options_on_long_press_app, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uninstall:
                Intent intent1 = new Intent(Intent.ACTION_DELETE);
                SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
                String packageName = prefs.getString("packageName", "Default Package Name");
                intent1.setData(Uri.parse("package:" + packageName));
                startActivity(intent1);
                return true;
            case R.id.addToHomeScreen:
                Toast.makeText(getApplicationContext(), "Option2", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void openAppDrawer() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_bottom, R.anim.fade_out)
                .replace(R.id.fragment_container, appDrawerFragment)
                .commit();
        allAppsButton.hide();
//        allAppsButton.setVisibility(View.GONE);
    }

    public void closeAppDrawer() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.exit_to_bottom, R.anim.fade_out)
                .replace(R.id.fragment_container, homeScreenFragment)
                .commit();
        allAppsButton.show();
//        allAppsButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        Log.i("MYINFO", "Back button pressed.");
        if (mainViewModel.isAppDrawerOpen.getValue()) {
            closeAppDrawer();
            mainViewModel.isAppDrawerOpen.setValue(false);
        } else if (mainViewModel.isPenaltyScreenOpen) {
            showHomeScreen();
            mainViewModel.isPenaltyScreenOpen = false;
        } else {
            // Do Nothing
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
                .addToBackStack(null)
                .commit();
        mainViewModel.isPenaltyScreenOpen = true;
        allAppsButton.hide();
//        allAppsButton.setVisibility(View.GONE);
    }

    @Override
    public void showHomeScreen() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.exit_to_bottom)
                .replace(R.id.fragment_container, homeScreenFragment)
                .commit();
        allAppsButton.show();
//        allAppsButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void showTimer() {
        TimerFragment timerFragment = new TimerFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                .replace(R.id.fragment_container, timerFragment)
                .addToBackStack(null)
                .commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        closeAppDrawer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeAppDrawer();
    }

    // Unused Function
    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
