package com.example.ppl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ppl.data.Serapan;
import com.example.ppl.data.SerapanResponse;
import com.example.ppl.data.UnitKerja;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineChartActivity extends AppCompatActivity {

    private LineChart lineChart;
    private Spinner spinnerSumber;
    private List<SerapanResponse> allData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        lineChart = findViewById(R.id.lineChart);
        spinnerSumber = findViewById(R.id.spinnerSumber);
        fetchChartData();
    }

    public CloseableHttpClient getUnsafeHttpClient() throws Exception {
        TrustStrategy trustAllStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(null, trustAllStrategy)
                .build();

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(
                        sslContext,
                        NoopHostnameVerifier.INSTANCE))
                .build();

        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(10))
                .build();

        PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setDefaultSocketConfig(socketConfig);

        connectionManager.setValidateAfterInactivity(Timeout.ofSeconds(30));

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }

    private void fetchChartData() {
        new Thread(() -> {
            try (CloseableHttpClient client = getUnsafeHttpClient()) {
                // Panggilan pertama ke API pertama
                URI uri1 = new URI("https://simkeu.unhas.ac.id:8110/serapan_universitas");
                HttpGet httpGet1 = new HttpGet(uri1);

                String jsonBody1 = "{ \"tahun\": 2024 }";
                StringEntity entity1 = new StringEntity(jsonBody1, ContentType.APPLICATION_JSON);
                httpGet1.setEntity(entity1);

                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("access_token", "");

                if (accessToken == null || accessToken.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "Token tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show());
                    return;
                }

                httpGet1.setHeader("Authorization", "Bearer " + accessToken);

                // Eksekusi API pertama
                try (CloseableHttpResponse response1 = client.execute(httpGet1)) {
                    int statusCode1 = response1.getCode();
                    if (statusCode1 == 200 && response1.getEntity() != null) {
                        String responseBody1 = EntityUtils.toString(response1.getEntity(), "UTF-8");

                        runOnUiThread(() -> {
                            allData = parseResponse(responseBody1);
                            if (allData != null) {
                                setupSpinner(allData);
                                displayChart(allData);
                            } else {
                                Toast.makeText(this, "Gagal memproses data API pertama!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Gagal memuat data API pertama! Kode: " + statusCode1, Toast.LENGTH_SHORT).show());
                    }
                }

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    private List<SerapanResponse> parseResponse(String responseBody) {
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<SerapanResponse>>() {}.getType();
            return gson.fromJson(responseBody, listType);
        } catch (Exception e) {
            Log.e("ParseResponse", "Error parsing response: ", e);
            return null;
        }
    }

    private void setupSpinner(List<SerapanResponse> data) {
        List<String> sumberNames = new ArrayList<>();
        sumberNames.add("Semua");
        for (SerapanResponse response : data) {
            sumberNames.add(response.getSumberNama());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sumberNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSumber.setAdapter(adapter);

        spinnerSumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    displayChart(allData);
                } else {
                    String selectedSumber = sumberNames.get(position);
                    List<SerapanResponse> filteredData = new ArrayList<>();
                    for (SerapanResponse response : allData) {
                        if (response.getSumberNama().equals(selectedSumber)) {
                            filteredData.add(response);
                        }
                    }
                    displayChart(filteredData);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void displayChart(List<SerapanResponse> data) {
        String selectedSumber = spinnerSumber.getSelectedItem().toString();

        if (selectedSumber.equals("Semua")) {
            // Mode "Semua": Total PaguNilai per bulan untuk semua sumber nama
            Map<String, float[]> sumberBulananMap = new HashMap<>();

            // Kelompokkan data per sumber dan bulan
            for (SerapanResponse response : data) {
                String sumberNama = response.getSumberNama();
                if (!sumberBulananMap.containsKey(sumberNama)) {
                    sumberBulananMap.put(sumberNama, new float[12]);
                }

                List<Serapan> serapanList = response.getSerapan();
                if (serapanList != null) {
                    for (Serapan serapan : serapanList) {
                        int bulan = serapan.getBulan() - 1;
                        if (bulan >= 0 && bulan < 12) {
                            try {
                                float nilai = Float.parseFloat(serapan.getNilai());
                                sumberBulananMap.get(sumberNama)[bulan] += nilai;
                            } catch (NumberFormatException e) {
                                Log.e("DisplayChart", "Invalid value for month: " + serapan.getNilai(), e);
                            }
                        }
                    }
                }
            }

            // Hitung total nilai per bulan dari semua sumber
            List<Entry> entries = new ArrayList<>();
            float[] totalBulanan = new float[12];
            for (Map.Entry<String, float[]> entry : sumberBulananMap.entrySet()) {
                float[] bulananValues = entry.getValue();
                for (int i = 0; i < 12; i++) {
                    totalBulanan[i] += bulananValues[i];
                }
            }

            for (int i = 0; i < 12; i++) {
                entries.add(new Entry(i, totalBulanan[i]));
            }

            // Nilai bulan terakhir untuk LimitLine
            float nilaiBulanTerakhir = totalBulanan[11];

            LineDataSet dataSet = new LineDataSet(entries, "Total PaguNilai per Bulan");
            dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
            dataSet.setCircleColor(getResources().getColor(android.R.color.holo_blue_dark));
            dataSet.setLineWidth(2f);
            dataSet.setValueTextSize(10f);

            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);

            // Konfigurasi sumbu X
            String[] bulanArray = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(true);
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(bulanArray));

            // Konfigurasi sumbu Y
            YAxis leftAxis = lineChart.getAxisLeft();
            leftAxis.setDrawGridLines(true);
            leftAxis.setAxisMinimum(0f);

            float maxValue = 0f;
            for (float value : totalBulanan) {
                if (value > maxValue) {
                    maxValue = value;
                }
            }
            leftAxis.setAxisMaximum(maxValue * 1.1f);

            leftAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format("%.1fM", value / 1_000_000);
                }
            });

            // Tambahkan LimitLine untuk bulan terakhir
            if (nilaiBulanTerakhir > 0) {
                leftAxis.removeAllLimitLines();
                LimitLine totalLine = new LimitLine(nilaiBulanTerakhir, "Total Bulan Terakhir");
                totalLine.setLineColor(getResources().getColor(android.R.color.holo_red_dark));
                totalLine.setLineWidth(2f);
                totalLine.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                totalLine.setTextSize(12f);
                leftAxis.addLimitLine(totalLine);
            }

            YAxis rightAxis = lineChart.getAxisRight();
            rightAxis.setEnabled(false);

            lineChart.getDescription().setEnabled(false);
            lineChart.setDragEnabled(true);
            lineChart.setScaleEnabled(true);
            lineChart.invalidate();

        } else {
            // Mode individu: Menampilkan nilai PaguNilai per bulan untuk sumber yang dipilih
            String[] bulanArray = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            List<Entry> entries = new ArrayList<>();
            float cumulativeValue = 0f;

            for (SerapanResponse response : data) {
                if (response.getSumberNama().equals(selectedSumber)) {
                    List<Serapan> serapanList = response.getSerapan();
                    if (serapanList != null) {
                        for (Serapan serapan : serapanList) {
                            int bulan = serapan.getBulan() - 1;
                            if (bulan >= 0 && bulan < 12) {
                                try {
                                    float nilai = Float.parseFloat(serapan.getNilai());
                                    cumulativeValue += nilai;
                                    entries.add(new Entry(bulan, cumulativeValue));
                                } catch (NumberFormatException e) {
                                    Log.e("DisplayChart", "Invalid value for month: " + serapan.getNilai(), e);
                                }
                            }
                        }
                    }
                    break;
                }
            }

            // Nilai bulan terakhir untuk LimitLine
            float nilaiBulanTerakhirIndividu = !entries.isEmpty() ? entries.get(entries.size() - 1).getY() : 0f;

            LineDataSet dataSet = new LineDataSet(entries, "Nilai Bulanan - " + selectedSumber);
            dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
            dataSet.setCircleColor(getResources().getColor(android.R.color.holo_blue_dark));
            dataSet.setLineWidth(2f);
            dataSet.setValueTextSize(10f);

            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);

            // Konfigurasi sumbu X
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(true);
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(bulanArray));

            // Konfigurasi sumbu Y
            YAxis leftAxis = lineChart.getAxisLeft();
            leftAxis.setDrawGridLines(true);
            leftAxis.setAxisMinimum(0f);

            float maxValue = 0f;
            for (Entry entry : entries) {
                if (entry.getY() > maxValue) {
                    maxValue = entry.getY();
                }
            }
            leftAxis.setAxisMaximum(maxValue * 1.1f);

            leftAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format("%.1fM", value / 1_000_000);
                }
            });

            // Tambahkan LimitLine untuk bulan terakhir
            if (nilaiBulanTerakhirIndividu > 0) {
                leftAxis.removeAllLimitLines();
                LimitLine totalLine = new LimitLine(nilaiBulanTerakhirIndividu, "Total Bulan Terakhir");
                totalLine.setLineColor(getResources().getColor(android.R.color.holo_red_dark));
                totalLine.setLineWidth(2f);
                totalLine.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                totalLine.setTextSize(12f);
                leftAxis.addLimitLine(totalLine);
            }

            YAxis rightAxis = lineChart.getAxisRight();
            rightAxis.setEnabled(false);

            lineChart.getDescription().setEnabled(false);
            lineChart.setDragEnabled(true);
            lineChart.setScaleEnabled(true);
            lineChart.invalidate();
        }
    }


}
