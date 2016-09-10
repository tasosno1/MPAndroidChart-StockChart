package com.stockchartdemo.stockchartdemo;

//Android Stock Chart Demo Using MPAndroidChart and Yahoo Chart data
//Created by Tasos 10-Sep-2016

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.stockchartdemo.stockchartdemo.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "MainActivity";
    public  String url, open, time, ticker;
    private LineChart mChart;
    private int mFillColor = Color.argb(150, 51, 181, 229);
    ArrayList<String> xValues = new ArrayList<String>();
    ArrayList<Entry> yVals1 = new ArrayList<Entry>();
    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mChart = (LineChart) findViewById(R.id.chart);
        mChart.setNoDataText("Creating Chart");

        makeChart();

    }

    private void makeChart() {

        ticker = "AAPL";

        url = "http://chartapi.finance.yahoo.com/instrument/1.0/" + ticker + "/chartdata;type=quote;range=1d/json";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                String data =  response.toString().substring(response.toString().lastIndexOf("\"series\" : [") + 1);

                String  newdata = data.replace("series\" : ", "");
                String  result = newdata.replace("\n" +
                        "} )", "");

                try {
                    JSONArray json = new JSONArray(result);

                    for (int i = 0; i <= json.length(); i++) {

                        JSONObject objv = json.getJSONObject(i);

                        time = objv.getString("Timestamp");

                        open = objv.getString("open");

                        float val = Float.parseFloat(open);

                        yVals1.add(new Entry(i, val));

                        long unixSeconds = Long.parseLong(time);
                        Date date = new Date(unixSeconds*1000L);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                        String formattedDate = sdf.format(date);

                        xValues.add(formattedDate);



                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(strReq);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                mChart.setBackgroundColor(Color.BLACK);
                mChart.setGridBackgroundColor(Color.BLACK);
                mChart.setDrawGridBackground(true);

                mChart.setDrawBorders(true);

                // no description text
                mChart.setDescription("");

                // if disabled, scaling can be done on x- and y-axis separately
                mChart.setPinchZoom(false);

                Legend l = mChart.getLegend();
                l.setEnabled(false);

                XAxis xAxis = mChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setAxisLineColor(Color.parseColor("#20F5F5F5"));
                xAxis.setTextColor(Color.parseColor("#50F5F5F5"));

                //final String[] xValues = new String[] { "a", "b", "c" };

                xAxis.setValueFormatter(new AxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return xValues.get((int) value % xValues.size());
                    }

                    @Override
                    public int getDecimalDigits() {
                        return 0;
                    }
                });



                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.setTextColor(Color.parseColor("#50F5F5F5"));
                leftAxis.setDrawAxisLine(true);
                leftAxis.setDrawZeroLine(false);
                leftAxis.setDrawGridLines(true);

                leftAxis.setGridColor(Color.parseColor("#20F5F5F5"));
                leftAxis.setAxisLineColor(Color.parseColor("#20F5F5F5"));

                mChart.getAxisRight().setEnabled(false);
                LineDataSet set1;

                // create a dataset and give it a type
                set1 = new LineDataSet(yVals1, "DataSet 1");

                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColor(Color.parseColor("#50FFEB3B"));
                set1.setDrawCircles(false);
                set1.setLineWidth(1f);
                set1.setCircleRadius(3f);
                set1.setFillAlpha(50);
                set1.setDrawFilled(true);
                set1.setFillColor(Color.BLUE);
                set1.setHighLightColor(Color.rgb(244, 117, 117));
                set1.setDrawCircleHole(false);


                dataSets.add(set1); // add the datasets



                LineData datab = new LineData(dataSets);
                datab.setDrawValues(false);

                mChart.setData(datab);

            }
        }, 1000);

    }

}