package launcher.astanite.com.astanite.ui;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import launcher.astanite.com.astanite.R;

public class IntroFragment extends Fragment {

    private String description, image_path;

    //private TextView mTitle, mDescription;
    private ImageView mImageView;
    private TextView mTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro, container, false);
        Bundle bundle = getArguments();


        description = bundle.getString("description");
        image_path = bundle.getString("image");
        mImageView = view.findViewById(R.id.iv_desc);
        mTextView = view.findViewById(R.id.tv_desc);

        try {
            InputStream inputStream = getContext().getAssets().open(image_path);
            mImageView.setImageBitmap(BitmapFactory.decodeStream(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mTextView.setText(description);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
