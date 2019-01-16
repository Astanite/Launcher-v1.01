package launcher.astanite.com.astanite.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.data.AppInfo;
import launcher.astanite.com.astanite.data.MyApplication;
import launcher.astanite.com.astanite.utils.Constants;
import launcher.astanite.com.astanite.viewmodel.MainViewModel;

import static android.content.Context.MODE_PRIVATE;

public class HomeScreenFragment extends Fragment implements TextWatcher {

    // code for updating intention inside onTextChange
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //updating intention in SharedPreference directly
        sharedPreferences.edit()
                .putString(Constants.KEY_INTENTION, intentionEditText.getText().toString())
                .apply();
        Log.d("upd Home", sharedPreferences.getString(Constants.KEY_INTENTION, ""));
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    interface PenaltyScreenListener {
        void showPenaltyScreen(int mode);
    }

    private static final String TAG = HomeScreenFragment.class.getSimpleName();

    private EditText intentionEditText;
    public static ProgressBar progressBar;
    private MainViewModel mainViewModel;
    private CompositeDisposable compositeDisposable;
    private PackageManager packageManager;
    private View rootview;
    public static ImageView ImageView1;
    public static ImageView ImageView2;
    public static ImageView ImageView3;
    public static ImageView ImageView4;
    private PenaltyScreenListener penaltyScreenListener;
    private SharedPreferences sharedPreferences;
    public List<AppInfo> homeScreenApps = new ArrayList<>();

    private CircleImageView iv_Mode, iv_FocusMode, iv_LeisureMode, iv_SleepMode;
    private Animation fabOpen, fabClose, rotateForward, rotateBackward;
    private boolean isOpen = false;
    private CircleImageView ivExitFocus, ivExitSleep, ivExitLeisure;
    private ImageView ivDataAnalysis ;
    private AppDrawerFragment.TimerScreenListener timerScreenListener;
    private AppDrawerFragment.SettingsScreenListener settingsScreenListener;

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        packageManager = getContext().getPackageManager();
        sharedPreferences = this.getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void setHomeScreenApps()
    {

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.3f);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        ImageView1.setColorFilter(filter);
        ImageView2.setColorFilter(filter);
        ImageView3.setColorFilter(filter);
        ImageView4.setColorFilter(filter);

        int size = homeScreenApps.size();
        SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit();
        if(size>0) {
            ImageView1.setImageDrawable(homeScreenApps.get(0).icon);
            ImageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //new
                    SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
                    String label = prefs.getString("Ha1label", " ");

                    if (label.equals("Phone"))
                        startActivity(new Intent(Intent.ACTION_DIAL));
                    else
                        startActivity(homeScreenApps.get(0).launchIntent);

                    //old
                    /*Intent launchIntent = new Intent(homeScreenApps.get(0).launchIntent);
                    startActivity(launchIntent);*/
                }
            });
            ImageView1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v)
                {
                    editor.putString("packageName",homeScreenApps.get(0).packageName);
                    editor.apply();
                    registerForContextMenu(ImageView1);
                    return false;
                }
            });
            if (size > 1) {
                ImageView2.setImageDrawable(homeScreenApps.get(1).icon);
                ImageView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //new
                        SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
                        String label = prefs.getString("Ha2label", " ");

                        if (label.equals("Phone"))
                            startActivity(new Intent(Intent.ACTION_DIAL));
                        else
                            startActivity(homeScreenApps.get(1).launchIntent);

                        //old
                    /*Intent launchIntent = new Intent(homeScreenApps.get(1).launchIntent);
                    startActivity(launchIntent);*/
                    }
                });
                ImageView2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        editor.putString("packageName",homeScreenApps.get(1).packageName);
                        editor.apply();
                        registerForContextMenu(ImageView2);
                        return false;
                    }
                });
                if (size > 2) {
                    ImageView3.setImageDrawable(homeScreenApps.get(2).icon);
                    ImageView3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
                            String label = prefs.getString("Ha3label", " ");

                            if (label.equals("Phone"))
                                startActivity(new Intent(Intent.ACTION_DIAL));
                            else
                                startActivity(homeScreenApps.get(2).launchIntent);

                            //old
                    /*Intent launchIntent = new Intent(homeScreenApps.get(2).launchIntent);
                    startActivity(launchIntent);*/
                        }
                    });
                    ImageView3.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v)
                        {
                            editor.putString("packageName",homeScreenApps.get(2).packageName);
                            editor.apply();
                            registerForContextMenu(ImageView3);
                            return false;
                        }
                    });
                    if (size > 3) {
                        ImageView4.setImageDrawable(homeScreenApps.get(3).icon);
                        ImageView4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
                                String label = prefs.getString("Ha4label", " ");

                                if (label.equals("Phone"))
                                    startActivity(new Intent(Intent.ACTION_DIAL));
                                else
                                    startActivity(homeScreenApps.get(3).launchIntent);

                                //old
                    /*Intent launchIntent = new Intent(homeScreenApps.get(3).launchIntent);
                    startActivity(launchIntent);*/
                            }
                        });
                        ImageView4.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v)
                            {
                                editor.putString("packageName",homeScreenApps.get(3).packageName);
                                editor.apply();
                                registerForContextMenu(ImageView4);
                                return false;
                            }
                        });
                    }
                }
            }
        }
        registerForContextMenu(ImageView1);
        registerForContextMenu(ImageView2);
        registerForContextMenu(ImageView3);
        registerForContextMenu(ImageView4);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.options_on_long_press_home_screen, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!(getActivity() instanceof PenaltyScreenListener))
            throw new ClassCastException("This activity is not a PenaltyScreenListener");
        else this.penaltyScreenListener = (PenaltyScreenListener) getActivity();

        mainViewModel = ViewModelProviders
                .of(getActivity())
                .get(MainViewModel.class);

        mainViewModel
                .getCurrentMode()
                .observe(this, mode -> {
                    if (mode != Constants.MODE_NONE) {
                        ImageView1.setVisibility(View.GONE);
                        ImageView2.setVisibility(View.GONE);
                        ImageView3.setVisibility(View.GONE);
                        ImageView4.setVisibility(View.GONE);

                        if(mode == Constants.MODE_FOCUS)
                        {
                            //iv_FocusMode.setAlpha(0.4f);
                            iv_LeisureMode.setAlpha(0.4f);
                            iv_SleepMode.setAlpha(0.4f);
                        }
                        else
                            if (mode == Constants.MODE_SLEEP)
                            {
                                iv_FocusMode.setAlpha(0.4f);
                                iv_LeisureMode.setAlpha(0.4f);
                                //iv_SleepMode.setAlpha(0.4f);
                            }
                            else
                                if(mode == Constants.MY_MODE)
                                {
                                    iv_FocusMode.setAlpha(0.4f);
                                  //  iv_LeisureMode.setAlpha(0.4f);
                                    iv_SleepMode.setAlpha(0.4f);
                                }
                    }
                });
        this.settingsScreenListener = (AppDrawerFragment.SettingsScreenListener) getActivity();
        this.timerScreenListener = (AppDrawerFragment.TimerScreenListener) getActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_screen, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        intentionEditText = view.findViewById(R.id.intentionEditText);
        intentionEditText.setText("");
        ImageView1 = view.findViewById(R.id.ImageView1);
        ImageView2 = view.findViewById(R.id.ImageView2);
        ImageView3 = view.findViewById(R.id.ImageView3);
        ImageView4 = view.findViewById(R.id.ImageView4);
        rootview = view;
        iv_Mode = view.findViewById(R.id.iv_modes);
        //entering mode buttons
        iv_FocusMode = view.findViewById(R.id.iv_focus_mode);
        iv_LeisureMode = view.findViewById(R.id.iv_leisure_mode);
        iv_SleepMode = view.findViewById(R.id.iv_sleep_mode);
        //exit mode buttons
        ivExitFocus = view.findViewById(R.id.exit_focus);
        ivExitLeisure = view.findViewById(R.id.exit_leisure);
        ivExitSleep = view.findViewById(R.id.exit_sleep);

        ImageView1.setVisibility(view.INVISIBLE);
        ImageView2.setVisibility(view.INVISIBLE);
        ImageView3.setVisibility(view.INVISIBLE);
        ImageView4.setVisibility(view.INVISIBLE);

        fabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_mode_open);
        fabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_mode_close);

        rotateForward = AnimationUtils.loadAnimation(getContext(), R.anim.mode_rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(getContext(), R.anim.mode_rotate_backward);
        rootview = view;

        progressBar = view.findViewById(R.id.pb_stats);

        intentionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    intentionEditText.setHint("");
                else
                    intentionEditText.setHint(R.string.default_intention);
            }
        });

        //entering different mode
        iv_Mode.setOnClickListener(view1 -> animateFab());
        iv_FocusMode.setOnClickListener(view2 -> {
            animateFab();

            int currmode = mainViewModel.getCurrentMode().getValue();
            if (currmode == Constants.MODE_NONE) {
                setClickable(mainViewModel.getCurrentMode().getValue());
                penaltyScreenListener.showPenaltyScreen(Constants.MODE_FOCUS);
            } else {

                if(currmode == Constants.MODE_FOCUS)
                {
                    ivExitFocus.performClick();
                    ivExitFocus.setVisibility(View.GONE);
                }
                else {
                    setClickable(mainViewModel.getCurrentMode().getValue());
                    Snackbar.make(view, "You're already in another mode", Snackbar.LENGTH_SHORT).show();
                }
            }

        });
        iv_SleepMode.setOnClickListener(view3 -> {
            animateFab();

            int currmode = mainViewModel.getCurrentMode().getValue();
            if (currmode == Constants.MODE_NONE) {
                setClickable(mainViewModel.getCurrentMode().getValue());
                penaltyScreenListener.showPenaltyScreen(Constants.MODE_SLEEP);
            } else {

                if(currmode == Constants.MODE_SLEEP)
                {
                    ivExitSleep.performClick();
                    ivExitSleep.setVisibility(View.GONE);
                }
                else {
                    setClickable(mainViewModel.getCurrentMode().getValue());
                    Snackbar.make(view, "You're already in another mode", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        iv_LeisureMode.setOnClickListener(view4 -> {
            animateFab();

            int currmode = mainViewModel.getCurrentMode().getValue();
            if (currmode == Constants.MODE_NONE) {
                setClickable(mainViewModel.getCurrentMode().getValue());
                penaltyScreenListener.showPenaltyScreen(Constants.MY_MODE);
            } else {

                if(currmode == Constants.MY_MODE)
                {
                    ivExitLeisure.performClick();
                    ivExitLeisure.setVisibility(View.GONE);
                }
                else {
                    setClickable(mainViewModel.getCurrentMode().getValue());
                    Snackbar.make(view, "You're already in another mode", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        //if user wants to exit current active mode.
        ivExitFocus.setOnClickListener(view5 -> {
            ExitMode(view);
            animateFab();
            changesaturation();
        });
        ivExitSleep.setOnClickListener(view6 -> {
            ExitMode(view);
            animateFab();
            changesaturation();
        });
        ivExitLeisure.setOnClickListener(view7 -> {
            ExitMode(view);
            animateFab();
            changesaturation();
        });

        intentionEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (event.getRawX() >= (intentionEditText.getRight() - intentionEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    ((MyApplication) getActivity().getApplication()).setSettingsmode(Constants.MODE_NONE);
                    settingsScreenListener.showSettings();
                    return true;
                }
            }
            return false;
        });
        SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        int removedAppIndex = 3;
        String removedPackageName = prefs.getString("removedPackageName","");
        int numberOfHomescreenApps = prefs.getInt("homeScreenApps",-1);
        Toast.makeText(getContext(),Integer.toString(homeScreenApps.size()),Toast.LENGTH_LONG);
        if(removedPackageName.compareTo("")!=0)
        {
            for(int i = 0; i < numberOfHomescreenApps; i++)
            {
                if(homeScreenApps.get(i).packageName.compareTo(removedPackageName)==0)
                {
                    removedAppIndex = i;
                    break;
                }
            }
            for(int i = removedAppIndex; i < numberOfHomescreenApps; i++)
            {
                SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit();
                String tempPackageName = prefs.getString("HomeApp"+Integer.toString(i+2),"");
                editor.putString("HomeApp"+Integer.toString(i+1),tempPackageName);
                String tempLabel = prefs.getString("Ha" + Integer.toString(i+2) + "label","");
                editor.putString("Ha" + Integer.toString(i+1) + "label", tempLabel);
                editor.apply();
            }
            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit();
            editor.putString("removedPackageName","");
            editor.putString("removedLabel", "");
            editor.apply();
        }
        homeScreenApps.clear();
        if(numberOfHomescreenApps > 0)
        {
            AppInfo appInfo = new AppInfo();
            appInfo.packageName = prefs.getString("HomeApp1","");
            try {
                appInfo.icon = packageManager.getApplicationIcon(appInfo.packageName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            appInfo.launchIntent = packageManager.getLaunchIntentForPackage(appInfo.packageName);
            homeScreenApps.add(appInfo);
            ImageView1.setVisibility(view.VISIBLE);
            if(numberOfHomescreenApps > 1)
            {
                AppInfo appInfo2 = new AppInfo();
                appInfo2.packageName = prefs.getString("HomeApp2","");
                try {
                    appInfo2.icon = packageManager.getApplicationIcon(appInfo2.packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                appInfo2.launchIntent = packageManager.getLaunchIntentForPackage(appInfo2.packageName);
                homeScreenApps.add(appInfo2);
                ImageView2.setVisibility(view.VISIBLE);
                if(numberOfHomescreenApps > 2)
                {
                    AppInfo appInfo3 = new AppInfo();
                    appInfo3.packageName = prefs.getString("HomeApp3","");
                    try {
                        appInfo3.icon = packageManager.getApplicationIcon(appInfo3.packageName);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    appInfo3.launchIntent = packageManager.getLaunchIntentForPackage(appInfo3.packageName);
                    homeScreenApps.add(appInfo3);
                    ImageView3.setVisibility(view.VISIBLE);
                    if(numberOfHomescreenApps > 3)
                    {
                        AppInfo appInfo4 = new AppInfo();
                        appInfo4.packageName = prefs.getString("HomeApp4","");
                        try {
                            appInfo4.icon = packageManager.getApplicationIcon(appInfo4.packageName);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        appInfo4.launchIntent = packageManager.getLaunchIntentForPackage(appInfo4.packageName);
                        homeScreenApps.add(appInfo4);
                        ImageView4.setVisibility(view.VISIBLE);
                    }
                }
            }
        }
        setHomeScreenApps();

    }

    private void ExitMode(View view) {
        long currentTime = System.currentTimeMillis();
        long enteredTime = mainViewModel.getTimeOfEnteringMode();
        long delta = currentTime - enteredTime;
        if (delta < mainViewModel.getModeTime() && delta != 0) {
            timerScreenListener.showTimer();
        } else {
            getContext().stopService(new Intent(getContext(), BlockingAppService.class));
            mainViewModel.setCurrentMode(Constants.MODE_NONE);
            Snackbar.make(view, "Exited mode", Snackbar.LENGTH_SHORT).show();
            progressBar.setProgress(0);
        }
    }

    private void setClickable(Integer mCurrMode) {
        if (mCurrMode != 0) {
            iv_FocusMode.setClickable(false);
            iv_SleepMode.setClickable(false);
            iv_LeisureMode.setClickable(false);
            ivExitFocus.setClickable(true);
            ivExitSleep.setClickable(true);
            ivExitLeisure.setClickable(true);
        } else {
            iv_FocusMode.setClickable(true);
            iv_SleepMode.setClickable(true);
            iv_LeisureMode.setClickable(true);
            //TODO dull the color of other two modes

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        intentionEditText.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        //updating the intention from shared preference
        intentionEditText.setText(sharedPreferences.getString(Constants.KEY_INTENTION, ""));
        intentionEditText.setEnabled(true);

        if(!isAccessGranted())
        {
            Log.e("permission time?", "Ask for it");
            final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.Dtheme)).create();
            alertDialog.setTitle("Permission required");
            alertDialog.setIcon(R.drawable.ic_security_black_24dp);
            alertDialog.setMessage("Astanite needs Usage Access for efficient performance.");
            alertDialog.setCancelable(false);
            alertDialog.setButton(Dialog.BUTTON_POSITIVE, "PERMIT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            });

            alertDialog.show();
        }

        if(mainViewModel.getCurrentMode().getValue() != 0)
        {
            progressBar.setProgress(calculateprogress());
        }

        int apparentMode = mainViewModel.getCurrentMode().getValue();
        int actualMode = sharedPreferences.getInt(Constants.KEY_CURRENT_MODE, Constants.MODE_NONE);

        if(apparentMode!=actualMode)
        {
            Log.e("Mode", "triggered ");

            switch (apparentMode)
            {
                case Constants.MODE_SLEEP:
                    mainViewModel.setModeTime(0,0);
                    ivExitSleep.performClick();
                    ivExitSleep.setVisibility(View.GONE);
                    break;
                case Constants.MODE_FOCUS:
                    mainViewModel.setModeTime(0,0);
                    ivExitFocus.performClick();
                    ivExitFocus.setVisibility(View.GONE);
                    break;
                case Constants.MY_MODE:
                    mainViewModel.setModeTime(0,0);
                    ivExitLeisure.performClick();
                    ivExitLeisure.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private Intent getDialerIntent() {
        TelecomManager manger = (TelecomManager) getContext().getSystemService(Context.TELECOM_SERVICE);
        String name = manger.getDefaultDialerPackage();
        return packageManager.getLaunchIntentForPackage(name);
    }

    private Drawable getDialerIcon() {
        Drawable icon = null;
        TelecomManager manger = (TelecomManager) getContext().getSystemService(Context.TELECOM_SERVICE);
        String name = manger.getDefaultDialerPackage();
        try {
            icon = packageManager.getApplicationIcon(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return icon;
    }


    @Override
    public void onStart() {
        super.onStart();
        intentionEditText.addTextChangedListener(this);
    }

    private void animateFab() {
        int currMode = mainViewModel.getCurrentMode().getValue();
        if (isOpen) {
            iv_Mode.startAnimation(rotateForward);
            iv_FocusMode.setVisibility(View.GONE);
            iv_SleepMode.setVisibility(View.GONE);
            iv_LeisureMode.setVisibility(View.GONE);
            iv_FocusMode.startAnimation(fabClose);
            iv_SleepMode.startAnimation(fabClose);
            iv_LeisureMode.startAnimation(fabClose);
            iv_FocusMode.setClickable(false);
            iv_LeisureMode.setClickable(false);
            iv_SleepMode.setClickable(false);

            if (Constants.MODE_FOCUS == currMode)
                ivExitFocus.setVisibility(View.GONE);
            else if (Constants.MODE_SLEEP == currMode)
                ivExitSleep.setVisibility(View.GONE);
            else if (Constants.MY_MODE == currMode)
                ivExitLeisure.setVisibility(View.GONE);

            //Code fragment responsible for mode transitions

            isOpen = false;
        } else {
            iv_Mode.startAnimation(rotateBackward);
            iv_FocusMode.setVisibility(View.VISIBLE);
            iv_SleepMode.setVisibility(View.VISIBLE);
            iv_LeisureMode.setVisibility(View.VISIBLE);
            iv_FocusMode.startAnimation(fabOpen);
            iv_SleepMode.startAnimation(fabOpen);
            iv_LeisureMode.startAnimation(fabOpen);
            iv_FocusMode.setClickable(true);
            iv_LeisureMode.setClickable(true);
            iv_SleepMode.setClickable(true);

            if (Constants.MODE_FOCUS == currMode)
                ivExitFocus.setVisibility(View.VISIBLE);
            else if (Constants.MODE_SLEEP == currMode)
                ivExitSleep.setVisibility(View.VISIBLE);
            else if (Constants.MY_MODE == currMode)
                ivExitLeisure.setVisibility(View.VISIBLE);
            isOpen = true;
        }
    }

    private void changesaturation(){
        iv_FocusMode.setAlpha(1f);
        iv_LeisureMode.setAlpha(1f);
        iv_SleepMode.setAlpha(1f);

        int size = homeScreenApps.size();

        Log.e("Triggered" , Integer.toString(size));

        if(size==1)
        {
            ImageView1.setVisibility(View.VISIBLE);
            ImageView2.setVisibility(View.INVISIBLE);
            ImageView3.setVisibility(View.INVISIBLE);
            ImageView4.setVisibility(View.INVISIBLE);
        }
        else
            if(size==2)
            {
                ImageView1.setVisibility(View.VISIBLE);
                ImageView2.setVisibility(View.VISIBLE);
                ImageView3.setVisibility(View.INVISIBLE);
                ImageView4.setVisibility(View.INVISIBLE);
            }
            else
                if(size==3)
                {
                    ImageView1.setVisibility(View.VISIBLE);
                    ImageView2.setVisibility(View.VISIBLE);
                    ImageView3.setVisibility(View.VISIBLE);
                    ImageView4.setVisibility(View.INVISIBLE);
                }
                else
                    if(size==4)
                    {
                        ImageView1.setVisibility(View.VISIBLE);
                        ImageView2.setVisibility(View.VISIBLE);
                        ImageView3.setVisibility(View.VISIBLE);
                        ImageView4.setVisibility(View.VISIBLE);
                    }
    }

    public int calculateprogress() {
        int x;
        long ctime = System.currentTimeMillis();
        long dtime = mainViewModel.getTimeOfEnteringMode();
        long delta = mainViewModel.getModeTime();

        long diff = ctime-dtime;

        if(diff>delta)
        {
            return 100;
        }
        else
        {
            float flag = 100*diff/delta;
            int temp = Math.round(flag);
            return temp;
        }
    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getContext().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getContext().getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getContext().getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
