package launcher.astanite.com.astanite.ui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.db.chart.model.LineSet;
import com.db.chart.util.Tools;
import com.db.chart.view.LineChartView;

import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.viewmodel.MainViewModel;

public class DataAnalysisFragment extends Fragment {

    private MainViewModel mainViewModel;
    LineChartView flagchart, totalchart;


    public DataAnalysisFragment() {
        // Required empty public constructor
    }

   /* @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!(getActivity() instanceof DataAnalysisFragment.HomeScreenListener)) throw new ClassCastException("This activity is not a homeScreenListener");
        else this.homeScreenListener = (DataAnalysisFragment.HomeScreenListener) getActivity();
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data_analysis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView uparrow = view.findViewById(R.id.uparrow);
        ImageView downarrow = view.findViewById(R.id.downarrow);

        TextView score = view.findViewById(R.id.score);
        String displayscore= getscore();
        score.setText(displayscore);

        int checkscore = Integer.parseInt(displayscore);
        if(checkscore>69)
        {
            downarrow.setImageAlpha(120);
        }
        else
            uparrow.setImageAlpha(120);

        output();

        flagchart = (LineChartView) view.findViewById(R.id.linechartflag);
        totalchart = (LineChartView) view.findViewById(R.id.linecharttotal);

        String[] mLabels = {"1", "2", "3", "4", "5", "6", "7"};
        float[] flagValues = getflagoutput();
        float[] totalValues = gettotaloutput();

        LineSet datasetflag = new LineSet(mLabels, flagValues);
        datasetflag.setColor(Color.parseColor("#FFC300"))
                .setFill(Color.parseColor("#FFC300"))
                .setThickness(0)
                .endAt(mLabels.length);
        flagchart.addData(datasetflag);

        LineSet datasettotal = new LineSet(mLabels, totalValues);
        datasettotal.setColor(Color.parseColor("#00C6FF"))
                .setFill(Color.parseColor("#00C6FF"))
                .setThickness(0)
                .endAt(mLabels.length);
        totalchart.addData(datasettotal);

        totalchart.setBorderSpacing(Math.round(Tools.fromDpToPx(15)))
                .setAxisBorderValues(0,10)
                .setLabelsColor(Color.parseColor("#0000ffff"))
                .setXAxis(false)
                .setYAxis(false);

        totalchart.show();

        flagchart.setBorderSpacing(Math.round(Tools.fromDpToPx(15)))
                .setAxisBorderValues(0,10)
                .setLabelsColor(Color.parseColor("#0000ffff"))
                .setXAxis(false)
                .setYAxis(false);

        flagchart.show();

        ImageView info = view.findViewById(R.id.info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.Dtheme)).create();
                alertDialog.setTitle("Astanite Analytics");
                alertDialog.setIcon(R.drawable.ic_poll_black_24dp);
                alertDialog.setMessage("This segment helps you understand your smartphone usage pattern.\n\nBlue segment: Total time spent on device\nYellow segment: Time spent on flagged apps\n\nEfficiency score: represents how efficiently you used your smartphone yesterday; calculated based on time spent and frequency of using various apps\n\n*Statistics refresh at midnight everyday");
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Got it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });
    }

    public void output()
    {
        SharedPreferences sharedpref = getContext().getSharedPreferences("datalysis", Context.MODE_PRIVATE);
        int thisindex = sharedpref.getInt("this",0);
        for(int i=0; i<7; i++)
        {
            String ftime = Float.toString(getflagtime(thisindex-i));
            Log.e("flag", ftime);

            String ttime = Float.toString(gettotaltime(thisindex-i));
            Log.e("total", ttime);
        }
    }

    public float[] getflagoutput()
    {
        SharedPreferences sharedpref = getContext().getSharedPreferences("datalysis", Context.MODE_PRIVATE);
        int thisindex = sharedpref.getInt("this",0);

        float[] apple = new float[7];
        for(int i=0; i<7; i++)
        {
            String ftime = Float.toString(getflagtime(thisindex-i));
            Log.e("flag", ftime);
            apple[i] = Float.parseFloat(ftime);
        }
        return apple;
    }

    public float[] gettotaloutput()
    {
        SharedPreferences sharedpref = getContext().getSharedPreferences("datalysis", Context.MODE_PRIVATE);
        int thisindex = sharedpref.getInt("this",0);

        float[] apple = new float[7];
        for(int i=0; i<7; i++)
        {
            String ttime = Float.toString(gettotaltime(thisindex-i));
            Log.e("total", ttime);
            apple[i] = Float.parseFloat(ttime);
        }
        return apple;
    }

    public String getscore()
    {
        SharedPreferences sharedpref = getContext().getSharedPreferences("datalysis", Context.MODE_PRIVATE);
        String score = sharedpref.getString("score", "0");
        Log.e("Score", score );
        return score;
    }

    public float getflagtime(int index)
    {
        SharedPreferences sharedpref = getContext().getSharedPreferences("datalysis", Context.MODE_PRIVATE);
        float time = sharedpref.getFloat(Integer.toString(index)+'f',0);
        return time;
    }

    public float gettotaltime(int index)
    {
        SharedPreferences sharedpref = getContext().getSharedPreferences("datalysis", Context.MODE_PRIVATE);
        float time = sharedpref.getFloat(Integer.toString(index)+'t',0);
        return time;
    }
}
