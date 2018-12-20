package launcher.astanite.com.astanite.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.utils.Constants;
import launcher.astanite.com.astanite.viewmodel.MainViewModel;

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
    private MainViewModel mainViewModel;
    private CompositeDisposable compositeDisposable;
    private PackageManager packageManager;
    private View rootview;
    private ImageView dialerImageView;
    private ImageView messagingImageView;
    private PenaltyScreenListener penaltyScreenListener;
    private SharedPreferences sharedPreferences;

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
                        messagingImageView.setVisibility(View.GONE);
                        dialerImageView.setVisibility(View.GONE);
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
        dialerImageView = view.findViewById(R.id.dialerImageView);
        messagingImageView = view.findViewById(R.id.messagingImageView);
        iv_Mode = view.findViewById(R.id.iv_modes);
        //entering mode buttons
        iv_FocusMode = view.findViewById(R.id.iv_focus_mode);
        iv_LeisureMode = view.findViewById(R.id.iv_leisure_mode);
        iv_SleepMode = view.findViewById(R.id.iv_sleep_mode);
        //exit mode buttons
        ivExitFocus = view.findViewById(R.id.exit_focus);
        ivExitLeisure = view.findViewById(R.id.exit_leisure);
        ivExitSleep = view.findViewById(R.id.exit_sleep);


        fabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_mode_open);
        fabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_mode_close);

        rotateForward = AnimationUtils.loadAnimation(getContext(), R.anim.mode_rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(getContext(), R.anim.mode_rotate_backward);
        rootview = view;

        //entering different mode
        iv_Mode.setOnClickListener(view1 -> animateFab());
        iv_FocusMode.setOnClickListener(view2 -> {
            animateFab();
            if (mainViewModel.getCurrentMode().getValue() == Constants.MODE_NONE) {
                setClickable(mainViewModel.getCurrentMode().getValue());
                penaltyScreenListener.showPenaltyScreen(Constants.MODE_FOCUS);
            } else {
                setClickable(mainViewModel.getCurrentMode().getValue());
                Snackbar.make(view, "You're already in another mode", Snackbar.LENGTH_SHORT).show();
            }

        });
        iv_SleepMode.setOnClickListener(view3 -> {
            animateFab();
            if (mainViewModel.getCurrentMode().getValue() == Constants.MODE_NONE) {
                setClickable(mainViewModel.getCurrentMode().getValue());
                penaltyScreenListener.showPenaltyScreen(Constants.MODE_SLEEP);
            } else {
                setClickable(mainViewModel.getCurrentMode().getValue());
                Snackbar.make(view, "You're already in another mode", Snackbar.LENGTH_SHORT).show();
            }
        });
        iv_LeisureMode.setOnClickListener(view4 -> {
            animateFab();
            if (mainViewModel.getCurrentMode().getValue() == Constants.MODE_NONE) {
                setClickable(mainViewModel.getCurrentMode().getValue());
                penaltyScreenListener.showPenaltyScreen(Constants.MY_MODE);
            } else {
                setClickable(mainViewModel.getCurrentMode().getValue());
                Snackbar.make(view, "You're already in another mode", Snackbar.LENGTH_SHORT).show();
            }
        });
        //if user wants to exit current active mode.
        ivExitFocus.setOnClickListener(view5 -> {
            ExitMode(view);
            animateFab();
        });
        ivExitSleep.setOnClickListener(view6 -> {
            ExitMode(view);
            animateFab();
        });
        ivExitLeisure.setOnClickListener(view7 -> {
            ExitMode(view);
            animateFab();
        });

        intentionEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (event.getRawX() >= (intentionEditText.getRight() - intentionEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    settingsScreenListener.showSettings();
                    return true;
                }
            }
            return false;
        });
        //setting phone and messages icons and click listeners
        Drawable dialerIcon = null;
        Drawable smsIcon = null;
        try {
            dialerIcon = getDialerIcon();
            smsIcon = packageManager.getApplicationIcon(Telephony.Sms.getDefaultSmsPackage(getContext()));
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Unable to load icons for dialer/sms");
        }

        Glide.with(this).load(smsIcon).into(messagingImageView);
        Glide.with(this).load(dialerIcon).into(dialerImageView);

        Intent i1 = getDialerIntent();
        dialerImageView.setOnClickListener(someView -> startActivity(i1));

        Intent i2 = packageManager.getLaunchIntentForPackage(Telephony.Sms.getDefaultSmsPackage(getContext()));
        messagingImageView.setOnClickListener(someView -> startActivity(i2));

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
        Log.d("update intention", intentionEditText.getText().toString());
        intentionEditText.setEnabled(true);
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
}
