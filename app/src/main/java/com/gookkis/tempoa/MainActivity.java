package com.gookkis.tempoa;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {

    private String TAG = "TemPOA";
    private String url = "http://agnosthings.com/ "+ "GANTI DENGAN API ANDA" +"/cel/";
    
    final String derajat = "\u00b0";
    Toolbar toolbar;
    private ProgressBar pv;
    private RelativeLayout rlRoot;
    private TextView tvSuhu;
    private FloatingActionButton btnReload;
    private LineChart lineChartSuhu, lineChartPakan;
    private int limit = 10;
    private RadioButton rb10, rb50, rb100, rb200, rb500, rbPakan, rbSuhu;
    Button btnAnalisa;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*aq = new AQuery(this);*/

        /*toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);*/

        pv = (ProgressBar) findViewById(R.id.progressBar);
        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);

        btnAnalisa = (Button) findViewById(R.id.btn_analisa);

        lineChartPakan = (LineChart) findViewById(R.id.line_chart_pakan);
        lineChartSuhu = (LineChart) findViewById(R.id.line_chart_suhu);

        tvSuhu = (TextView) findViewById(R.id.text_suhu);

        btnReload = (FloatingActionButton) findViewById(R.id.fabReload);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetChartAsyncTask().execute();
            }
        });

        url = url + limit;

        rb10 = (RadioButton) findViewById(R.id.rbPlot10);
        rb50 = (RadioButton) findViewById(R.id.rbPlot50);
        rb100 = (RadioButton) findViewById(R.id.rbPlot100);
        rb200 = (RadioButton) findViewById(R.id.rbPlot200);
        rb500 = (RadioButton) findViewById(R.id.rbPlot500);

        rbPakan = (RadioButton) findViewById(R.id.rbDataPakan);
        rbSuhu = (RadioButton) findViewById(R.id.rbDataSuhu);

        rb10.setOnClickListener(new OnTimeIntervalRadioButtonListener());
        rb50.setOnClickListener(new OnTimeIntervalRadioButtonListener());
        rb100.setOnClickListener(new OnTimeIntervalRadioButtonListener());
        rb200.setOnClickListener(new OnTimeIntervalRadioButtonListener());
        rb500.setOnClickListener(new OnTimeIntervalRadioButtonListener());

        rbPakan.setOnClickListener(new OnTimeIntervalRadioButtonListener());
        rbSuhu.setOnClickListener(new OnTimeIntervalRadioButtonListener());

        new GetChartAsyncTask().execute();

        btnAnalisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetAnalisaAsyncTask().execute();
            }
        });
    }

    private class OnTimeIntervalRadioButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if (rb10.isChecked()) {
                limit = 10;
            } else if (rb50.isChecked()) {
                limit = 50;
            } else if (rb100.isChecked()) {
                limit = 100;
            } else if (rb200.isChecked()) {
                limit = 200;
            } else if (rb500.isChecked()) {
                limit = 500;
            } else {
                limit = 10;
            }

            if (rbPakan.isChecked()) {
                url = "http://agnosthings.com/"+ "GANTI DENGAN GUID ANDA" +"/channel/limit/feed/"+ "GANTI DENGAN API ANDA" +"/cm/" + limit;
            } else if (rbSuhu.isChecked()) {
                url = "http://agnosthings.com/"+ "GANTI DENGAN GUID ANDA" +"/channel/limit/feed/"+ "GANTI DENGAN API ANDA" +"/cel/" + limit;
            } else {
                url = "http://agnosthings.com/"+ "GANTI DENGAN GUID ANDA" +"/channel/limit/feed/"+ "GANTI DENGAN API ANDA" +"/cel/" + limit;
            }

            new GetChartAsyncTask().execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    private class GetChartAsyncTask extends AsyncTask<Void, Void, float[]> {
        float suhu;

        @Override
        protected void onPreExecute() {
            pv.setVisibility(View.VISIBLE);
            rlRoot.setVisibility(View.INVISIBLE);
        }

        @Override
        protected float[] doInBackground(Void... arg) {

            // Chosen time interval
            final float[] values = new float[limit];
            try {
                String respon = run(url);
                Gson gson = new Gson();

                PakanModel suhuModel = gson.fromJson(respon, PakanModel.class);
                Log.d(TAG, "loadChart: " + respon);

                for (int i = 0; i < limit; i++) {
                    String[] cel = suhuModel.getValue().split(",");
                    //Log.d(TAG, "onParse: " + cel[i]);
                    values[i] = Float.parseFloat(cel[i]);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String urlSuhu = "http://agnosthings.com/"+ "GANTI DENGAN GUID ANDA" +"/field/last/feed/"+ "GANTI DENGAN API ANDA" +"/cel";
            try {
                String respon = run(urlSuhu);
                JSONObject json = new JSONObject(respon);
                suhu = Float.parseFloat(json.getString("value"));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return values;

        }


        @Override
        protected void onPostExecute(float[] values) {

            //Log.d(TAG, "onPostExecute: " + values[0]);
            // Turns off progress view and sets price chart visible
            pv.setVisibility(View.INVISIBLE);
            rlRoot.setVisibility(View.VISIBLE);
            tvSuhu.setText("Suhu : " + suhu + derajat + "C");

            if (rbPakan.isChecked()) {
                lineChartPakan.setVisibility(View.VISIBLE);
                lineChartSuhu.setVisibility(View.INVISIBLE);
                ArrayList<Entry> entries = new ArrayList<>();
                for (int i = 0; i < values.length; i++) {
                    entries.add(new Entry(10 - values[i], i));
                }

                LineDataSet dataset = new LineDataSet(entries, "cm");
                ArrayList<String> labels = new ArrayList<String>();
                for (int i = 0; i < values.length; i++) {
                    int j = i + 1;
                    labels.add("" + j);
                }

                dataset.setColor(ColorTemplate.getHoloBlue());
                dataset.setLineWidth(2f);
                LineData data = new LineData(labels, dataset);
                YAxis leftAxis = lineChartPakan.getAxisLeft();
                leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines

                leftAxis.setAxisMinValue(-5f);

                lineChartPakan.setData(data);
                lineChartPakan.setDescription(" cm");
                lineChartPakan.animateY(3000);
            } else {
                lineChartPakan.setVisibility(View.INVISIBLE);
                lineChartSuhu.setVisibility(View.VISIBLE);

                ArrayList<Entry> entries = new ArrayList<>();
                for (int i = 0; i < values.length; i++) {
                    entries.add(new Entry(values[i], i));
                }

                LineDataSet dataset = new LineDataSet(entries, derajat + "C");
                ArrayList<String> labels = new ArrayList<String>();
                for (int i = 0; i < values.length; i++) {
                    int j = i + 1;
                    labels.add("" + j);
                }

                dataset.setColor(getResources().getColor(R.color.colorAccent));
                dataset.setLineWidth(2f);
                LineData data = new LineData(labels, dataset);
                lineChartSuhu.setData(data);
                lineChartSuhu.setDescription(derajat + "C");
                lineChartSuhu.animateY(3000);
                lineChartSuhu.setTouchEnabled(true);

                // enable scaling and dragging
                lineChartSuhu.setDragEnabled(true);
                lineChartSuhu.setScaleEnabled(true);


               /* LimitLine ll1 = new LimitLine(40f, "Batas Atas");
                ll1.setLineWidth(4f);
                ll1.enableDashedLine(1f, 1f, 0f);
                ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                ll1.setTextSize(8f);


                LimitLine ll2 = new LimitLine(20f, "Batas Bawah");
                ll1.setLineWidth(4f);
                ll1.enableDashedLine(1f, 1f, 0f);
                ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
                ll2.setTextSize(8f);

                YAxis leftAxis = lineChartSuhu.getAxisLeft();
                leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
                leftAxis.addLimitLine(ll1);
                leftAxis.addLimitLine(ll2);
                leftAxis.setAxisMaxValue(50f);
                leftAxis.setAxisMinValue(15f);*/

            }

        }

    }

    private class GetAnalisaAsyncTask extends AsyncTask<Void, Void, float[]> {
        final float[] valuesSuhu = new float[limit];
        final float[] valuesPakan = new float[limit];
        String urlPakan = "http://agnosthings.com/"+ "GANTI DENGAN GUID ANDA" +"/channel/limit/feed/"+ "GANTI DENGAN API ANDA" +"/cm/" + limit;
        String urlSuhu = "http://agnosthings.com/"+ "GANTI DENGAN GUID ANDA" +"/channel/limit/feed/"+ "GANTI DENGAN API ANDA" +"/cel/" + limit;

        @Override
        protected void onPreExecute() {
            pv.setVisibility(View.VISIBLE);
            rlRoot.setVisibility(View.INVISIBLE);
        }

        @Override
        protected float[] doInBackground(Void... arg) {

            // Chosen time interval
            try {
                String respon = run(urlPakan);
                Gson gson = new Gson();

                PakanModel suhuModel = gson.fromJson(respon, PakanModel.class);
                Log.d(TAG, "loadChart: Pakan" + respon);

                for (int i = 0; i < limit; i++) {
                    String[] cel = suhuModel.getValue().split(",");
                    //Log.d(TAG, "onParse: " + cel[i]);
                    valuesPakan[i] = Float.parseFloat(cel[i]);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                String respon = run(urlSuhu);
                Gson gson = new Gson();

                SuhuModel suhuModel = gson.fromJson(respon, SuhuModel.class);
                Log.d(TAG, "loadChart: Suhu " + respon);

                for (int i = 0; i < limit; i++) {
                    String[] cel = suhuModel.getValue().split(",");
                    //Log.d(TAG, "onParse: " + cel[i]);
                    valuesSuhu[i] = Float.parseFloat(cel[i]);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return valuesSuhu;

        }


        @Override
        protected void onPostExecute(float[] values) {

            //Log.d(TAG, "onPostExecute: " + values[0]);
            // Turns off progress view and sets price chart visible
            pv.setVisibility(View.INVISIBLE);
            rlRoot.setVisibility(View.VISIBLE);
            tvSuhu.setText("Suhu : " + valuesSuhu[0] + derajat + "C");
            lineChartPakan.setVisibility(View.VISIBLE);
            lineChartSuhu.setVisibility(View.INVISIBLE);

            ArrayList<Entry> entriesPakan = new ArrayList<>();
            for (int i = 0; i < valuesPakan.length; i++) {
                entriesPakan.add(new Entry(10 - valuesPakan[i], i));
            }

            ArrayList<Entry> entriesSuhu = new ArrayList<>();
            for (int i = 0; i < valuesSuhu.length; i++) {
                entriesSuhu.add(new Entry(valuesSuhu[i], i));
            }


            ArrayList<String> xVals = new ArrayList<String>();
            for (int i = 0; i < valuesSuhu.length; i++) {
                xVals.add((i) + "");
            }
            //LineDataSet dataset = new LineDataSet(entriesPakan, derajat + "C");


            /*
            ArrayList<String> labels = new ArrayList<String>();
            for (int i = 0; i < valuesPakan.length; i++) {
                int j = i + 1;
                labels.add("" + j);
            }

            dataset.setColor(getResources().getColor(R.color.colorAccent));

            LineData data = new LineData(labels, dataset);
            lineChartPakan.setData(data);
            lineChartPakan.setDescription(" cm");
            lineChartPakan.animateY(3000);*/

            LineDataSet set1, set2;
            set1 = new LineDataSet(entriesPakan, "Pakan");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            //set1.setCircleColor(Color.GREEN);
            set1.setLineWidth(2f);
            //set1.setValueTextColor(Color.GREEN);
            //set1.setCircleRadius(3f);
            //set1.setFillAlpha(65);
            //set1.setFillColor(ColorTemplate.getHoloBlue());
            //set1.setHighLightColor(Color.rgb(244, 117, 117));
            //set1.setDrawCircleHole(false);
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
            set2 = new LineDataSet(entriesSuhu, "Suhu");
            set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set2.setColor(Color.RED);
            //set2.setCircleColor(Color.YELLOW);
            //set2.setValueTextColor(Color.YELLOW);
            set2.setLineWidth(2f);
            //set2.setCircleRadius(3f);
            //set2.setFillAlpha(65);
            //set2.setFillColor(Color.RED);
            //set2.setDrawCircleHole(false);
            //set2.setHighLightColor(Color.rgb(244, 117, 117));
            //set2.setFillFormatter(new MyFillFormatter(900f));

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set2);
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(xVals, dataSets);
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(8f);

            // set data
            lineChartPakan.setData(data);
            lineChartPakan.setDescription("Analisa Trend Pakan dan Suhu");


        }


    }


    String run(String url) throws IOException {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Request request = new Request.Builder()
                .url(url)
                .build();

        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }


}