package launcher.astanite.com.astanite.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding3.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import io.reactivex.disposables.CompositeDisposable;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.utils.Constants;
import launcher.astanite.com.astanite.viewmodel.MainViewModel;

public class HomeScreenFragment extends Fragment {

    interface PenaltyScreenListener {
        void showPenaltyScreen(int mode);
    }

    private static final String TAG = HomeScreenFragment.class.getSimpleName();

    private EditText intentionEditText;
    private MainViewModel mainViewModel;
    private MaterialButton focusModeButton;
    private MaterialButton workModeButton;
    private MaterialButton sleepModeButton;
    private MaterialButton myModeButton;
    private CompositeDisposable compositeDisposable;
    PackageManager packageManager;
    private View rootview;
    private ImageView dialerImageView;
    private ImageView messagingImageView;
    private PenaltyScreenListener penaltyScreenListener;

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        packageManager = getContext().getPackageManager();
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
                    setActiveModeButton(mode);
                });
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        focusModeButton = view.findViewById(R.id.focusModeButton);
        workModeButton = view.findViewById(R.id.workModeButton);
        sleepModeButton = view.findViewById(R.id.sleepModeButton);
        myModeButton = view.findViewById(R.id.myModeButton);
        intentionEditText = view.findViewById(R.id.intentionEditText);
        dialerImageView = view.findViewById(R.id.dialerImageView);
        messagingImageView = view.findViewById(R.id.messagingImageView);
        rootview = view;

        compositeDisposable.add(RxTextView.textChanges(intentionEditText)
                .debounce(1, TimeUnit.SECONDS)
                .subscribe(text -> mainViewModel.updateIntention(text.toString())));

        focusModeButton.setOnClickListener(someView -> {
            if (mainViewModel.getCurrentMode().getValue() == Constants.MODE_NONE) {
                Log.d(TAG, "Showing penalty screen for mode: " + Constants.MODE_FOCUS);
                penaltyScreenListener.showPenaltyScreen(Constants.MODE_FOCUS);
            } else
                Snackbar.make(view, "You're already in another mode", Snackbar.LENGTH_SHORT).show();
        });
        workModeButton.setOnClickListener(someView -> {
            if (mainViewModel.getCurrentMode().getValue() == Constants.MODE_NONE)
                penaltyScreenListener.showPenaltyScreen(Constants.MODE_WORK);
            else
                Snackbar.make(view, "You're already in another mode", Snackbar.LENGTH_SHORT).show();
        });
        sleepModeButton.setOnClickListener(someView -> {
            if (mainViewModel.getCurrentMode().getValue() == Constants.MODE_NONE)
                penaltyScreenListener.showPenaltyScreen(Constants.MODE_SLEEP);
            else
                Snackbar.make(view, "You're already in another mode", Snackbar.LENGTH_SHORT).show();
        });
        myModeButton.setOnClickListener(someView -> {
            if (mainViewModel.getCurrentMode().getValue() == Constants.MODE_NONE)
                penaltyScreenListener.showPenaltyScreen(Constants.MY_MODE);
            else
                Snackbar.make(view, "You're already in another mode", Snackbar.LENGTH_SHORT).show();
        });


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

    @Override
    public void onPause() {
        super.onPause();
        intentionEditText.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
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

    public void setActiveModeButton(int newMode) {
        Log.d(TAG, "Applying color for mode: " + newMode);
        switch (newMode) {
            case Constants.MODE_FOCUS:
                Log.d(TAG, "Enabling on focus button");
                focusModeButton.setEnabled(true);
                workModeButton.setEnabled(false);
                sleepModeButton.setEnabled(false);
                myModeButton.setEnabled(false);
                break;
            case Constants.MODE_WORK:
                focusModeButton.setEnabled(false);
                workModeButton.setEnabled(true);
                sleepModeButton.setEnabled(false);
                myModeButton.setEnabled(false);
                break;
            case Constants.MODE_SLEEP:
                focusModeButton.setEnabled(false);
                workModeButton.setEnabled(false);
                sleepModeButton.setEnabled(true);
                myModeButton.setEnabled(false);
                break;
            case Constants.MY_MODE:
                focusModeButton.setEnabled(false);
                workModeButton.setEnabled(false);
                sleepModeButton.setEnabled(false);
                myModeButton.setEnabled(true);
                break;
            default:
                focusModeButton.setEnabled(true);
                workModeButton.setEnabled(true);
                sleepModeButton.setEnabled(true);
                myModeButton.setEnabled(true);
                break;
        }
    }
}
