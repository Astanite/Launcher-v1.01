package launcher.astanite.com.astanite.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.triggertrap.seekarc.SeekArc;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.utils.Constants;
import launcher.astanite.com.astanite.viewmodel.MainViewModel;

import static android.content.Context.MODE_PRIVATE;

/*
CREATED BY UTSAV
 */

public class PenaltyFragment extends Fragment {

    private static final String TAG = PenaltyFragment.class.getSimpleName();

    interface HomeScreenListener {
        void showHomeScreen();
    }

    SeekArc seekArc;
    SeekBar seekBar;
    ImageView play;
    int hrs, mins, exitl;
    private int modeForPenaltyScreen;

    private MainViewModel mainViewModel;
    private HomeScreenListener homeScreenListener;
    private ScreenOnOffReceiver mScreenReceiver;

    public PenaltyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!(getActivity() instanceof HomeScreenListener))
            throw new ClassCastException("This class is not a HomeScreenListener");
        else this.homeScreenListener = (HomeScreenListener) getActivity();

        mainViewModel = ViewModelProviders
                .of(getActivity())
                .get(MainViewModel.class);
        mainViewModel.penaltyScreenTriggeredForMode
                .observe(this, mode -> {
                    Log.d(TAG, "Penalty Screen Triggered for mode: " + mode);
                    this.modeForPenaltyScreen = mode;
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_penalty, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        seekArc = view.findViewById(R.id.seekArc);
        seekBar = view.findViewById(R.id.seekbar);
        final TextView duration = view.findViewById(R.id.duration);
        final TextView level = view.findViewById(R.id.ExitBarrierlevel);

        int defaultcolor = Color.parseColor("#b3ffffff");
        int progresscolor = Color.parseColor("#ffffff");

        play = view.findViewById(R.id.enter);

        seekArc.setArcColor(defaultcolor);
        seekArc.setProgressColor(progresscolor);
        seekArc.setArcWidth(10);
        seekArc.setProgressWidth(10);
        seekArc.setProgress(getarc());

        int defp = getarc()*360;
        defp = defp/100;

        int h = defp/60;
        defp = defp%60;

        String temp = Integer.toString(h) + " h : " + Integer.toString(defp) + " m";
        duration.setText(temp);

        seekBar.setProgress(getbar());
        level.setText(Integer.toString(getbar()));

        seekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
                int m = i*360;
                m = m/100;

                int h = m/60;
                m = m%60;

                String temp = Integer.toString(h) + " h : " + Integer.toString(m) + " m";
                Log.e("TAG", temp);
                duration.setText(temp);
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                savearc(seekArc.getProgress());
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String temp = Integer.toString(progress);
                level.setText(temp);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                savebar(seekBar.getProgress());
            }
        });


        play.setOnClickListener(v -> {
            int temparc = seekArc.getProgress();
            mins = temparc*360;
            mins = mins/100;

            hrs = mins/60;
            mins = mins%60;

            int tempbar = seekBar.getProgress();
            exitl = tempbar/14;



            Log.e("Time", Integer.toString(hrs) + ':' + Integer.toString(mins) );
            Log.e("Penalty",Integer.toString(exitl));

            savedesired(exitl);

            mainViewModel.setCurrentMode(modeForPenaltyScreen);
            mainViewModel.setModeTime(hrs, mins);
            homeScreenListener.showHomeScreen();
            //Write the code for starting a service to block not allowed apps
            Log.d("Service started", " Again");

            mScreenReceiver = new ScreenOnOffReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            getContext().registerReceiver(mScreenReceiver, filter);

            getContext().startService(new Intent(getContext(), BlockingAppService.class));
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        mainViewModel.penaltyScreenTriggeredForMode.setValue(0);
    }


    public class ScreenOnOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (Objects.equals(intent.getAction(), Intent.ACTION_SCREEN_OFF)) {
                Log.d("StackOverflow", "Screen Off");
                getContext().stopService(new Intent(getContext(), BlockingAppService.class));
                getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                        .edit()
                        .putString("last_mode", String.valueOf(getContext()
                                .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                                .getInt(Constants.KEY_CURRENT_MODE, Constants.MODE_NONE)))
                        .apply();
            } else if (Objects.equals(intent.getAction(), Intent.ACTION_SCREEN_ON)) {
                SharedPreferences shared = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
                String check_last_mode = shared.getString("last_mode", "0");
                if (!check_last_mode.equals("0")) {
                    Log.d("StackOverflow", "Screen On");
                    getContext().startService(new Intent(getContext(), BlockingAppService.class));
                }
            }
        }
    }

    public void savearc(int arc)
    {
        SharedPreferences sharedpref = getContext().getSharedPreferences("penalty", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putInt("arc", arc);
        editor.apply();
    }

    public void savebar(int bar)
    {
        SharedPreferences sharedpref = getContext().getSharedPreferences("penalty", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putInt("bar", bar);
        editor.apply();
    }

    public void savedesired(int desired)
    {
        SharedPreferences sharedpref = getContext().getSharedPreferences("difficulty", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putInt("getdesired", desired);
        editor.apply();
    }

    public int getarc()
    {
        SharedPreferences sharedpref = getContext().getSharedPreferences("penalty", Context.MODE_PRIVATE);
        int temp = sharedpref.getInt("arc",40);
        return temp;
    }

    public int getbar()
    {
        SharedPreferences sharedpref = getContext().getSharedPreferences("penalty", Context.MODE_PRIVATE);
        int temp = sharedpref.getInt("bar",70);
        return temp;
    }
}