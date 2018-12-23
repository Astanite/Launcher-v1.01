package launcher.astanite.com.astanite.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.ui.settings.FlaggedAppsFragment;
import launcher.astanite.com.astanite.ui.settings.SettingsViewModel;
import launcher.astanite.com.astanite.utils.Constants;

public class IntroActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CardView mlogin;
    private ImageView mDot1, mDot2, mDot3, mDot4;

    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    FirebaseAuth.AuthStateListener mAuthListener;

    private SettingsViewModel settingsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);

        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        mViewPager = findViewById(R.id.intro_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        mDot1.setImageResource(R.drawable.intro_dot_selected);
                        mDot2.setImageResource(R.drawable.intro_dot_default);
                        mDot3.setImageResource(R.drawable.intro_dot_default);
                        mDot4.setImageResource(R.drawable.intro_dot_default);
                        break;
                    case 1:
                        mDot2.setImageResource(R.drawable.intro_dot_selected);
                        mDot1.setImageResource(R.drawable.intro_dot_default);
                        mDot3.setImageResource(R.drawable.intro_dot_default);
                        mDot4.setImageResource(R.drawable.intro_dot_default);
                        break;
                    case 2:
                        mDot3.setImageResource(R.drawable.intro_dot_selected);
                        mDot1.setImageResource(R.drawable.intro_dot_default);
                        mDot2.setImageResource(R.drawable.intro_dot_default);
                        mDot4.setImageResource(R.drawable.intro_dot_default);
                        break;
                    case 3:
                        mDot4.setImageResource(R.drawable.intro_dot_selected);
                        mDot1.setImageResource(R.drawable.intro_dot_default);
                        mDot3.setImageResource(R.drawable.intro_dot_default);
                        mDot2.setImageResource(R.drawable.intro_dot_default);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mlogin = findViewById(R.id.cv_google_login);
        mlogin.setOnClickListener(v -> {
            signIn();
            Toast.makeText(this, "Please Wait!", Toast.LENGTH_SHORT).show();
//            settingsViewModel.currentFragment.setValue(Constants.FRAGMENT_DISTRACTIVE_APPS);
//            settingsViewModel.currentMode.setValue(Constants.DISTRACTIVE_APP);
//            Bundle argument = new Bundle();
//            argument.putBoolean("isDist", true);
//            FlaggedAppsFragment flaggedAppsFragment = new FlaggedAppsFragment();
//
//            flaggedAppsFragment.setArguments(argument);
//            Log.d("isDist", String.valueOf(true));
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
//                    .replace(R.id.settingsContainer, flaggedAppsFragment)
//                    .commit();
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    Toast.makeText(IntroActivity.this, "Hello " + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                    mlogin.setVisibility(View.GONE);
                    //Login successful now move to homeActivity
                    Intent intent = new Intent(IntroActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    mlogin.setVisibility(View.VISIBLE);
                }
            }
        };
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(IntroActivity.this, "Login Error!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mDot1 = findViewById(R.id.dot1);
        mDot2 = findViewById(R.id.dot2);
        mDot3 = findViewById(R.id.dot3);
        mDot4 = findViewById(R.id.dot4);

        settingsViewModel = ViewModelProviders
                .of(this)
                .get(SettingsViewModel.class);
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private Context mContext;

        public SectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new IntroFragment();
            Bundle bundle = new Bundle();

            switch (position) {

                case 0:
                    bundle.putString("image", "intro/ic_contact_us.png");
                    bundle.putString("description", "lorem ipsum dummy text 1");
                    break;
                case 1:
                    bundle.putString("image", "intro/ic_contact_us.png");
                    bundle.putString("description", "lorem ipsum dummy text 2");
                    break;
                case 2:
                    bundle.putString("image", "intro/ic_contact_us.png");
                    bundle.putString("description", "lorem ipsum dummy text 3");
                    break;
                case 3:
                    bundle.putString("image", "intro/ic_contact_us.png");
                    bundle.putString("description", "lorem ipsum dummy text 4");
                    break;
            }
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(IntroActivity.this, "Login Error!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mlogin.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(IntroActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}
