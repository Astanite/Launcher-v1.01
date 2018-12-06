package launcher.astanite.com.astanite.ui.settings;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import launcher.astanite.com.astanite.R;

public class FlaggedContactsFragment extends Fragment {


    public FlaggedContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flagged_contacts, container, false);
    }

}
