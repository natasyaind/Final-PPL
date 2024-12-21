package com.example.ppl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ppl.admin.AdminAnalysisFragment;
import com.example.ppl.data.Serapan;
import com.example.ppl.data.SerapanResponse;
import com.example.ppl.data.UnitKerja;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.fragment.app.FragmentTransaction;

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

public class PieChartActivity extends AppCompatActivity {

    private LineChart lineChart;
    private Spinner spinnerSumber,spinnerSumber_unit_kerja;
    private List<SerapanResponse> allData = new ArrayList<>();
    private List<UnitKerja> anotherData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        lineChart = findViewById(R.id.lineChart);
        spinnerSumber = findViewById(R.id.spinnerSumber_1);
        spinnerSumber_unit_kerja = findViewById(R.id.spinnerSumber_2);

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
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("access_token", "");

                if (accessToken == null || accessToken.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "Token tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Panggilan kedua ke API kedua
                URI uri2 = new URI("https://simkeu.unhas.ac.id:8110/list_unit_kerja");
                HttpGet httpPost2 = new HttpGet(uri2);

                String jsonBody2 = "{ \"tahun\": \"2024\" }";
                StringEntity entity2 = new StringEntity(jsonBody2, ContentType.APPLICATION_JSON);
                httpPost2.setEntity(entity2);

                httpPost2.setHeader("Authorization", "Bearer " + accessToken);

                // Eksekusi API kedua
                try (CloseableHttpResponse response2 = client.execute(httpPost2)) {
                    int statusCode2 = response2.getCode();
                    if (statusCode2 == 200 && response2.getEntity() != null) {
                        String responseBody2 = EntityUtils.toString(response2.getEntity(), "UTF-8");

                        runOnUiThread(() -> {
                            anotherData = parseukResponse(responseBody2);
                            if (anotherData != null) {
                                List<String> unitKerjaList = new ArrayList<>();
                                Map<String, String> unitKerjaMap = new HashMap<>();
                                for (UnitKerja unit : anotherData) {
                                    unitKerjaList.add(unit.getName());
                                    unitKerjaMap.put(unit.getName(), unit.getCode());
                                }
                                setupSpinneruk(unitKerjaList, unitKerjaMap);
                            } else {
                                Toast.makeText(this, "Gagal memproses data API kedua!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Gagal memuat data API kedua! Kode: " + statusCode2, Toast.LENGTH_SHORT).show());
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
    private List<UnitKerja> parseukResponse(String responseBody) {
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

            List<UnitKerja> unitKerjaList = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String code = entry.getKey();
                String name = entry.getValue().getAsString();
                unitKerjaList.add(new UnitKerja(code, name));
            }

            return unitKerjaList;
        } catch (Exception e) {
            Log.e("ParseukResponse", "Error parsing response: " + responseBody, e);
            return null;
        }
    }

    private void fetchSerapanData(String unitKerjaCode) {
        new Thread(() -> {
            try (CloseableHttpClient client = getUnsafeHttpClient()) {
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("access_token", "");

                if (accessToken == null || accessToken.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "Token tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show());
                    return;
                }

                URI uri = new URI("https://simkeu.unhas.ac.id:8110/serapan_unit_kerja");
                HttpGet httpGet = new HttpGet(uri);

                // Siapkan body JSON untuk request
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("tahun", 2024); // Tahun tetap
                jsonBody.put("unitkerja", unitKerjaCode); // Kode unit kerja dipilih dari spinner

                StringEntity entity = new StringEntity(jsonBody.toString(), ContentType.APPLICATION_JSON);
                httpGet.setEntity(entity);

                httpGet.setHeader("Authorization", "Bearer " + accessToken);

                try (CloseableHttpResponse response = client.execute(httpGet)) {
                    int statusCode = response.getCode();
                    if (statusCode == 200 && response.getEntity() != null) {
                        String responseBody3 = EntityUtils.toString(response.getEntity(), "UTF-8");

                        // Parse data response
                        List<SerapanResponse> serapanData = parseResponse(responseBody3);

                        // Tampilkan data, misalnya di chart
                        runOnUiThread(() -> {
                            if (serapanData != null) {
                                displayChart(serapanData);
                                setupSpinner(serapanData);// Tampilkan data pada chart
                            } else {
                                Toast.makeText(this, "Gagal memproses data serapan!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Gagal memuat data serapan! Kode: " + statusCode, Toast.LENGTH_SHORT).show());
                    }
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void setupSpinner(List<SerapanResponse> data) {
        // Menggunakan Set untuk menghindari duplikat
        Set<String> sumberNamesSet = new LinkedHashSet<>();
        sumberNamesSet.add("Semua"); // Tambahkan opsi "Semua" di awal

        // Loop melalui data untuk mendapatkan sumberNama dari setiap Serapan
        for (SerapanResponse response : data) {
            if (response.getSumberNama() != null && !response.getSumberNama().isEmpty()) {
                sumberNamesSet.add(response.getSumberNama());
            }
        }

        // Konversi Set ke List untuk digunakan di Adapter
        List<String> sumberNames = new ArrayList<>(sumberNamesSet);

        // Atur adapter untuk spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sumberNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSumber.setAdapter(adapter);

        spinnerSumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSumber = sumberNames.get(position);

                if (selectedSumber.equals("Semua")) {
                    displayChart(data); // Tampilkan semua data
                } else {
                    List<SerapanResponse> filteredData = new ArrayList<>();
                    for (SerapanResponse response : data) {
                        if (response.getSumberNama().equals(selectedSumber)) {
                            filteredData.add(response);
                        }
                    }
                    displayChart(filteredData); // Tampilkan data yang difilter
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Tidak melakukan apa-apa jika tidak ada yang dipilih
            }
        });

        // Tampilkan data default ("Semua") saat spinner selesai diatur
        displayChart(data);

        // Pilih item default (mode "Semua")
        spinnerSumber.setSelection(0);
    }

    private void setupSpinneruk(List<String> unitKerjaList, Map<String, String> unitKerjaMap) {
        // Tambahkan opsi "Semua" di awal list
        List<String> sumberNames = new ArrayList<>();
        sumberNames.add("Semua");
        sumberNames.addAll(unitKerjaList); // Tambahkan semua unit kerja setelah "Semua"

        Spinner spinner = findViewById(R.id.spinnerSumber_2);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sumberNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // Opsi "Semua" dipilih
                    fetchSerapanData(null); // Panggil API dengan null atau logika khusus untuk semua unit kerja
                } else {
                    String selectedUnitKerja = sumberNames.get(position);
                    String unitKerjaCode = unitKerjaMap.get(selectedUnitKerja); // Ambil kode unit kerja berdasarkan nama
                    fetchSerapanData(unitKerjaCode); // Panggil API kedua
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Tidak ada aksi
            }
        });
    }

    private void displayChart(List<SerapanResponse> data) {
        // Reset grafik dan data setiap kali metode dipanggil
        lineChart.clear();
        lineChart.invalidate();

        // Periksa apakah spinnerSumber sudah memiliki item yang dipilih
        if (spinnerSumber == null || spinnerSumber.getSelectedItem() == null) {
            Log.e("DisplayChart", "Spinner sumber tidak memiliki item yang dipilih atau belum diinisialisasi");
            return; // Keluar dari metode jika spinner tidak valid
        }

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

            // Hitung total nilai per bulan dari semua sumber dan akumulasi
            List<Entry> entries = new ArrayList<>();
            float[] totalBulanan = new float[12];
            for (Map.Entry<String, float[]> entry : sumberBulananMap.entrySet()) {
                float[] bulananValues = entry.getValue();
                for (int i = 0; i < 12; i++) {
                    totalBulanan[i] += bulananValues[i];
                }
            }

            // Hitung nilai kumulatif per bulan
            float cumulativeValue = 0f;
            for (int i = 0; i < 12; i++) {
                cumulativeValue += totalBulanan[i];
                entries.add(new Entry(i, cumulativeValue));
            }

            float nilaiBulanTerakhir = cumulativeValue;

            LineDataSet dataSet = new LineDataSet(entries, "Total PaguNilai Kumulatif per Bulan");
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
            leftAxis.setAxisMaximum(nilaiBulanTerakhir * 1.1f);

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
            // Mode individu: Menampilkan nilai kumulatif per bulan untuk sumber yang dipilih
            // Mode individu: Menampilkan nilai kumulatif per bulan untuk sumber yang dipilih
            List<Entry> entries = new ArrayList<>();
            float[] bulananValues = new float[12];
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
                                    bulananValues[bulan] += nilai; // Akumulasi nilai per bulan
                                } catch (NumberFormatException e) {
                                    Log.e("DisplayChart", "Invalid value for month: " + serapan.getNilai(), e);
                                }
                            }
                        }
                    }
                    break;
                }
            }

// Hitung nilai kumulatif per bulan
            for (int i = 0; i < 12; i++) {
                cumulativeValue += bulananValues[i];
                entries.add(new Entry(i, cumulativeValue));
            }

            float nilaiBulanTerakhirIndividu = cumulativeValue;

            LineDataSet dataSet = new LineDataSet(entries, "Nilai Kumulatif - " + selectedSumber);
            dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
            dataSet.setCircleColor(getResources().getColor(android.R.color.holo_blue_dark));
            dataSet.setLineWidth(2f);
            dataSet.setValueTextSize(10f);

            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);

// Konfigurasi sumbu X (sama seperti mode "Semua")
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
            leftAxis.setAxisMaximum(nilaiBulanTerakhirIndividu * 1.1f);

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