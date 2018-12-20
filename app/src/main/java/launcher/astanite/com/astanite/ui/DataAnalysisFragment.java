package launcher.astanite.com.astanite.ui;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import launcher.astanite.com.astanite.R;

public class DataAnalysisFragment extends Fragment {


    public DataAnalysisFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data_analysis, container, false);
    }

}
