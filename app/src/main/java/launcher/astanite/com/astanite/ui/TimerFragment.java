package launcher.astanite.com.astanite.ui;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.utils.Constants;
import launcher.astanite.com.astanite.viewmodel.MainViewModel;

public class TimerFragment extends Fragment {

    private MainViewModel mainViewModel;
    private long delta;
    private TextView timertv;
    private CountDownTimer cdt;
    private PenaltyFragment.HomeScreenListener homeScreenListener;

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!(getActivity() instanceof PenaltyFragment.HomeScreenListener)) throw new ClassCastException("This activity is not a homeScreenListener");
        else this.homeScreenListener = (PenaltyFragment.HomeScreenListener) getActivity();

        mainViewModel = ViewModelProviders
                .of(getActivity())
                .get(MainViewModel.class);

        delta = mainViewModel.getPenalty();
        cdt = new CountDownTimer(delta, 1000) {

            public void onTick(long millisUntilFinished) {
                timertv.setText(millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                mainViewModel.setCurrentMode(Constants.MODE_NONE);
                homeScreenListener.showHomeScreen();
            }

            public void canceltimer(){
                if(cdt!=null)
                    cdt.cancel();
            }
        }.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        timertv = view.findViewById(R.id.timer);
    }
}
