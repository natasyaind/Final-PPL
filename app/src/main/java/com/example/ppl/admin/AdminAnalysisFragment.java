package com.example.ppl.admin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
//import com.example.ppl.LineChartActivity;
import com.example.ppl.LineChartActivity;
import com.example.ppl.PieChartActivity;
import com.example.ppl.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class AdminAnalysisFragment extends Fragment {

    private Button btn_pie, btn_line;
//    private Spinner spinner;
//    private RequestQueue requestQueue;
//    private HashMap<String, String> dropdownMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analysis, container, false);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BarChart barChart = view.findViewById(R.id.barChart);
        btn_pie = view.findViewById(R.id.btn_pie);
        btn_line = view.findViewById(R.id.btn_line);
//        spinner = view.findViewById(R.id.spinner);

        btn_pie.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PieChartActivity.class);
            startActivity(intent);
        });

        btn_line.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LineChartActivity.class);
            startActivity(intent);
        });

        ArrayList<BarEntry> visitors = new ArrayList<>();
        visitors.add(new BarEntry(2017, 420));
        visitors.add(new BarEntry(2018, 475));
        visitors.add(new BarEntry(2019, 500));
        visitors.add(new BarEntry(2020, 650));
        visitors.add(new BarEntry(2021, 550));
        visitors.add(new BarEntry(2022, 630));
        visitors.add(new BarEntry(2023, 470));

        BarDataSet barDataSet = new BarDataSet(visitors, "Visitors");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);

//        if (isNetworkAvailable()) {
//            fetchDropdownData();
//        } else {
//            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
//        }


//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedValue = parent.getItemAtPosition(position).toString();
//                String selectedKey = null;
//
//                for (Map.Entry<String, String> entry : dropdownMap.entrySet()) {
//                    if (entry.getValue().equals(selectedValue)) {
//                        selectedKey = entry.getKey();
//                        break;
//                    }
//                }
//
//                Toast.makeText(requireContext(), "Selected: " + selectedKey + " - " + selectedValue, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });

    }

//    private void fetchDropdownData() {
//        String url = "https://simkeu.unhas.ac.id:8110/list_unit_kerja";
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                response -> {
//                    try {
//                        ArrayList<String> dropdownItems = new ArrayList<>();
//                        dropdownMap.clear();
//
//                        Iterator<String> keys = response.keys();
//                        while (keys.hasNext()) {
//                            String key = keys.next();
//                            String value = response.getString(key);
//
//                            dropdownMap.put(key, value); // Simpan key dan value
//                            dropdownItems.add(value); // Tambahkan value ke Spinner
//                        }
//
//                        // Set data ke Spinner
//                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, dropdownItems);
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        spinner.setAdapter(adapter);
//
//                    } catch (JSONException e) {
//                        Log.e("PARSE_ERROR", "Error parsing response", e);
//                        Toast.makeText(requireContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
//                    }
//                },
//                error -> {
//                    Log.e("API_ERROR", "Error fetching data", error);
//                    Toast.makeText(requireContext(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//        );
//
//        requestQueue.add(jsonObjectRequest);
//    }

//    private OkHttpClient getUnsafeOkHttpClient() {
//        try {
//            // Create an untrusted SSL context
//            TrustManager[] trustAllCertificates = new TrustManager[]{
//                    new X509TrustManager() {
//                        public X509Certificate[] getAcceptedIssuers() {
//                            return null;
//                        }
//
//                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                        }
//
//                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                        }
//                    }
//            };
//
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
//
//            // Set up OkHttpClient to use the untrusted SSL context
//            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCertificates[0]);
//            builder.hostnameVerifier((hostname, session) -> true); // Ignore hostname verification
//
//            return builder.build();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to setup unsafe OkHttpClient", e);
//        }
//    }


}