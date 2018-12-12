package launcher.astanite.com.astanite.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.viewmodel.MainViewModel;

/*
CREATED BY UTSAV
 */

public class PenaltyFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = PenaltyFragment.class.getSimpleName();

    interface HomeScreenListener {
        void showHomeScreen();
    }

    private static NumberPicker np, npm;
    private int modeForPenaltyScreen;
    private int hrs, mins;
    private int pen;

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
        np = (NumberPicker) view.findViewById(R.id.numberPicker1);
        npm = (NumberPicker) view.findViewById(R.id.numberPicker2);
        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ImageView b = (ImageView) view.findViewById(R.id.enterModeButton);
        b.setOnClickListener(v -> {
            hrs = np.getValue();
            mins = npm.getValue();
            mainViewModel.setCurrentMode(modeForPenaltyScreen);
            mainViewModel.setModeTime(hrs, mins);
            mainViewModel.setPenalty(pen);
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

        np.setMaxValue(5);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);

        npm.setMaxValue(59);
        npm.setMinValue(0);
        npm.setWrapSelectorWheel(false);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.penaltyops, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                pen = 1;
                break;
            case 1:
                pen = 2;
                break;
            case 2:
                pen = 5;
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        pen = 2;
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
            } else if (Objects.equals(intent.getAction(), Intent.ACTION_SCREEN_ON)) {
                Log.d("StackOverflow", "Screen On");
                getContext().startService(new Intent(getContext(), BlockingAppService.class));
            }
        }
    }
}