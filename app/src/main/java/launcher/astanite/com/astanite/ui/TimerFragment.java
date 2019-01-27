package launcher.astanite.com.astanite.ui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.utils.Constants;
import launcher.astanite.com.astanite.viewmodel.MainViewModel;

public class TimerFragment extends Fragment {

    private MainViewModel mainViewModel;
    private PenaltyFragment.HomeScreenListener homeScreenListener;

    public TextView et;
    public ImageView zero, one, two, three, four, five, six, seven, eight, nine, button, info;
    String answer;
    ConstraintLayout keypad;
    View answerblank;

    public int d=3,ans=0,count = 0,correct=0;
    public int desired;
    public long stime, dtime, delay=15000;
    public TextView tv,timer,overtv;

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!(getActivity() instanceof PenaltyFragment.HomeScreenListener)) throw new ClassCastException("This activity is not a homeScreenListener");
        else this.homeScreenListener = (PenaltyFragment.HomeScreenListener) getActivity();

        /*mainViewModel = ViewModelProviders
                .of(getActivity())
                .get(MainViewModel.class);

//        delta = mainViewModel.getPenalty();
        delta = 500 ;
        cdt = new CountDownTimer(delta, 1000) {

            public void onTick(long millisUntilFinished) {
                timertv.setText(millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                mainViewModel.setCurrentMode(Constants.MODE_NONE);
                Log.d("_Service_ ", "Stopped");

                getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("isServiceStopped", true)
                        .apply();
                (getContext()).stopService(new Intent(getContext(), BlockingAppService.class));
                homeScreenListener.showHomeScreen();
            }

            public void canceltimer(){
                if(cdt!=null)
                    cdt.cancel();
            }
        }.start();*/

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

        zero = view.findViewById(R.id.zero);
        one = view.findViewById(R.id.one);
        two = view.findViewById(R.id.two);
        three = view.findViewById(R.id.three);
        four = view.findViewById(R.id.four);
        five = view.findViewById(R.id.five);
        six = view.findViewById(R.id.six);
        seven = view.findViewById(R.id.seven);
        eight = view.findViewById(R.id.eight);
        nine = view.findViewById(R.id.nine);
        button = view.findViewById(R.id.submit);

        keypad = view.findViewById(R.id.keypad);
        answerblank = view.findViewById(R.id.answerblank);
        info = view.findViewById(R.id.info);

        et = view.findViewById(R.id.answerblanktv);
        tv = view.findViewById(R.id.tv);
        answer = "";

        mainViewModel = ViewModelProviders
                .of(getActivity())
                .get(MainViewModel.class);

        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer= answer+'0';
                et.setText(answer);
            }
        });

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer= answer+'1';
                et.setText(answer);
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer= answer+'2';
                et.setText(answer);
            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer= answer+'3';
                et.setText(answer);
            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer= answer+'4';
                et.setText(answer);
            }
        });

        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer= answer+'5';
                et.setText(answer);
            }
        });

        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer= answer+'6';
                et.setText(answer);
            }
        });

        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer= answer+'7';
                et.setText(answer);
            }
        });

        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer= answer+'8';
                et.setText(answer);
            }
        });

        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer= answer+'9';
                et.setText(answer);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper( getContext(), R.style.Dtheme)).create();
                alertDialog.setTitle("Exit Barrier");
                alertDialog.setIcon(R.drawable.ic_access_time_black_24dp);
                alertDialog.setMessage("Are you sure you want to exit?");
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Got it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });

        d = getdiff();
        desired = getDesired();

        //The first question
        correct=question();

        //Click submit button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Converting answer to intiger
                String temp = et.getText().toString();
                try {
                    ans = Integer.parseInt(et.getText().toString());
                } catch(NumberFormatException nfe) {
                }

                //Log.e(Integer.toString(ans), Integer.toString(correct));

                //Checking if the answer is correct
                if (ans == correct)
                {

                    Log.e(Integer.toString(ans), Integer.toString(correct));

                    count++;

                    {
                        long etime = System.currentTimeMillis();
                        dtime = etime - stime; //Time taken in solving question
                        //Log.e("stime", Long.toString(stime));
                        //Log.e("etime", Long.toString(etime));
                        long time = dtime / 1000; //Time taken in seconds
                        //Log.e("Time:", Long.toString(time));

                        //Increasing or decreasing difficulty
                        {
                            if (time > 30 && d > 1)
                                d--;
                            else if (d < 6)
                                d++;
                        }

                        savediff(d);
                    }


                    Toast.makeText(getContext(), "Your answer is correct!" , Toast.LENGTH_SHORT ).show();
                    et.setText(null);

                    invisible();

                    if(count<desired)
                    {
                        new CountDownTimer(delay, 1000) {
                            public void onFinish() {

                                visible();
                                et.setText("");
                                correct=question();
                            }

                            public void onTick(long millisUntilFinished) {
                                et.setText("00 : " + millisUntilFinished / 1000);

                            }
                        }.start();
                    }
                    else
                    {
                        onfinish();
                    }



                }

                //Conditiom for wrong answer
                else
                {
                    Toast.makeText(getContext(), "Wrong answer" , Toast.LENGTH_SHORT ).show();
                    et.setText(null);

                }

                answer = "";

            }
        });

    }



    public void savediff(int d)
    {
        SharedPreferences sharedpref = getContext().getSharedPreferences("difficulty", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putInt("diff", d);
        editor.apply();
    }

    //Function to get difficulty
    public int getdiff()
    {
        SharedPreferences sharedpref = getContext().getSharedPreferences("difficulty", Context.MODE_PRIVATE);
        int temp = sharedpref.getInt("diff",2);
        return temp;
    }

    //Function to get penalty levels
    public int getDesired()
    {
        SharedPreferences sharedpref = getContext().getSharedPreferences("difficulty", Context.MODE_PRIVATE);
        int temp = sharedpref.getInt("getdesired",1);
        return temp;
    }

    // 5 Levels of questions
    int level1()
    {
        Random rand = new Random();
        int one = rand.nextInt(89) +11;
        int two = rand.nextInt(89) +11;
        int correct=one+two;
        String output = Integer.toString(one)+'+'+Integer.toString(two);
        tv.setText(output);
        return correct;
    }

    int level2()
    {
        Random rand = new Random();
        int one = rand.nextInt(89) +11;
        int two = rand.nextInt(89) +11;

        int correct;

        if(one>two) {
            correct = one - two;
            String output = Integer.toString(one) + '-' + Integer.toString(two);
            tv.setText(output);
        }

        else {
            correct = two - one;
            String output = Integer.toString(two) + '-' + Integer.toString(one);
            tv.setText(output);
        }
        return correct;
    }

    int level3()
    {
        Random rand = new Random();
        int one = rand.nextInt(89) +11;
        int two = rand.nextInt(89) +11;
        int three = rand.nextInt(89) +11;

        int correct;

        if(one>two) {
            correct = three + one - two;
            String output = Integer.toString(three) + '+' + Integer.toString(one) + '-' + Integer.toString(two);
            tv.setText(output);
        }

        else {
            correct = three + two - one;
            String output = Integer.toString(three) + '+' + Integer.toString(two) + '-' + Integer.toString(one);
            tv.setText(output);
        }
        return correct;
    }

    int level4()
    {
        Random rand = new Random();
        int one = rand.nextInt(89) +11;
        int two = rand.nextInt(6) +4;
        int correct=one*two;
        String output = Integer.toString(one)+'x'+Integer.toString(two);
        tv.setText(output);
        return correct;
    }

    int level5()
    {
        Random rand = new Random();
        int one = rand.nextInt(89) +11;
        int two = rand.nextInt(89) +11;
        int correct=one*two;
        String output = Integer.toString(one)+'x'+Integer.toString(two);
        tv.setText(output);
        return correct;
    }

    int level6()
    {
        Random rand = new Random();
        int one = rand.nextInt(89) +11;
        int two = rand.nextInt(89) +11;
        int three = rand.nextInt(89) +11;

        int correct;

        if(one>two) {
            correct = three*(one - two);
            String output = Integer.toString(three) + "x(" + Integer.toString(one) + '-' + Integer.toString(two) + ')';
            tv.setText(output);
        }

        else {
            correct = three*(two - one);
            String output = Integer.toString(three) + "x(" + Integer.toString(two) + '-' + Integer.toString(one) + ')';
            tv.setText(output);
        }
        return correct;
    }



    // Main function to supply questions
    public int question()
    {
        int correct=0;
        switch (d)
        {
            case 1: correct= level1(); break;
            case 2: correct= level2(); break;
            case 3: correct= level3(); break;
            case 4: correct= level4(); break;
            case 5: correct= level5(); break;
            case 6: correct= level6(); break;
        }

        stime = System.currentTimeMillis();


        return correct;
    }

    public void invisible()
    {
        keypad.setVisibility(View.INVISIBLE);
        answerblank.setVisibility(View.INVISIBLE);
        tv.setVisibility(View.INVISIBLE);
    }

    public void visible()
    {
        keypad.setVisibility(View.VISIBLE);
        answerblank.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
    }

    public void onfinish()
    {
        mainViewModel.setCurrentMode(Constants.MODE_NONE);
        Log.d("_Service_ ", "Stopped");

        getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean("isServiceStopped", true)
                .apply();
        (getContext()).stopService(new Intent(getContext(), BlockingAppService.class));
        homeScreenListener.showHomeScreen();

        HomeScreenFragment.progressBar.setProgress(0);
        HomeScreenFragment.ImageView1.setVisibility(View.VISIBLE);
        HomeScreenFragment.ImageView2.setVisibility(View.VISIBLE);
        HomeScreenFragment.ImageView3.setVisibility(View.VISIBLE);
        HomeScreenFragment.ImageView4.setVisibility(View.VISIBLE);

    }
}
