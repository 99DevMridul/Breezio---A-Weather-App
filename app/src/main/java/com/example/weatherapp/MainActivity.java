package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView tempText, cityText, humidityText, windText, pressureText, descText, tipText;
    TextView weatherStatusText;

    ImageView weatherIcon;
    EditText cityInput;
    Button searchBtn;

    String API_KEY = "da5feff731a4af184a6fdda87b415557";
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempText = findViewById(R.id.tempText);
        cityText = findViewById(R.id.cityText);
        humidityText = findViewById(R.id.humidityText);
        windText = findViewById(R.id.windText);
        pressureText = findViewById(R.id.pressureText);
        descText = findViewById(R.id.descText);
        weatherIcon = findViewById(R.id.weatherIcon);
        tipText = findViewById(R.id.tipText); // New TextView for tips

        weatherStatusText =findViewById(R.id.weatherStatusText);

        cityInput = findViewById(R.id.cityInput);
        searchBtn = findViewById(R.id.searchBtn);

        prefs = getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE);
        String lastCity = prefs.getString("lastCity", "Kolkata");
        cityInput.setText(lastCity);
        fetchWeather(lastCity);

        searchBtn.setOnClickListener(v -> {
            String city = cityInput.getText().toString().trim();
            if (!city.isEmpty()) {
                prefs.edit().putString("lastCity", city).apply(); // save city
                fetchWeather(city);
            }
        });
    }

    private void fetchWeather(String cityName) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + API_KEY;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        JSONObject main = jsonObj.getJSONObject("main");
                        JSONObject wind = jsonObj.getJSONObject("wind");
                        JSONArray weather = jsonObj.getJSONArray("weather");

                        double temp = main.getDouble("temp") - 273.15;
                        int humidity = main.getInt("humidity");
                        int pressure = main.getInt("pressure");
                        double windSpeed = wind.getDouble("speed");
                        String desc = weather.getJSONObject(0).getString("description");
                        String icon = weather.getJSONObject(0).getString("icon");

                        tempText.setText((int) temp + "°C");
                        cityText.setText(jsonObj.getString("name") + ", " +
                                jsonObj.getJSONObject("sys").getString("country"));
                        humidityText.setText("Humidity\n" + humidity + "%");
                        windText.setText("Wind\n" + windSpeed + " m/s");
                        pressureText.setText("Pressure\n" + pressure + " hPa");
                        descText.setText("Info\n" + desc);

                        setWeatherIcon(icon);
                        setTipOfTheDay(icon);

                    } catch (Exception e) {
                        cityText.setText("Parsing Error");
                    }
                },
                error -> cityText.setText("City not found / Network error"));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void setWeatherIcon(String icon) {
        switch (icon) {
            case "01d":
                weatherIcon.setImageResource(R.drawable.ic_sunny);
                weatherStatusText.setText("Sunny");
                break;
            case "01n":
                weatherIcon.setImageResource(R.drawable.ic_clear_night);
                weatherStatusText.setText("Clear Night");
                break;
            case "02d":
            case "02n":
            case "03d":
            case "03n":
            case "04d":
            case "04n":
                weatherIcon.setImageResource(R.drawable.ic_cloud);
                weatherStatusText.setText("Cloudy");
                break;
            case "09d":
            case "09n":
            case "10d":
            case "10n":
                weatherIcon.setImageResource(R.drawable.ic_rain);
                weatherStatusText.setText("Rainy");
                break;
            case "11d":
            case "11n":
                weatherIcon.setImageResource(R.drawable.ic_storm);
                weatherStatusText.setText("Stormy");
                break;
            case "13d":
            case "13n":
                weatherIcon.setImageResource(R.drawable.ic_snow);
                weatherStatusText.setText("Snowy");
                break;
            case "50d":
            case "50n":
                weatherIcon.setImageResource(R.drawable.ic_mist);
                weatherStatusText.setText("Misty");
                break;
            default:
                weatherIcon.setImageResource(R.drawable.ic_cloud);
                weatherStatusText.setText("Unknown");
        }
    }


    private void setTipOfTheDay(String icon) {
        String tip;

        if (icon.contains("n")) {
            tip = "Good night! Have a restful sleep.";
        }
        else if (icon.equals("01d")) {
            tip = "It's sunny! Stay hydrated and wear sunscreen if you go outside.";
        }
        else if (icon.equals("02d") || icon.equals("03d") || icon.equals("04d")) {
            tip = "It's cloudy. A calm day to relax or go for a walk.";
        }
        else if (icon.equals("09d") || icon.equals("10d")) {
            tip = "It's raining. Carry an umbrella if you step out!";
        }
        else if (icon.equals("11d")) {
            tip = "Stormy weather! Better to stay indoors and stay safe.";
        }
        else if (icon.equals("50d")) {
            tip = "Misty day ahead. Drive carefully, visibility may be low.";
        }
        else {
            tip = "Have a great day!";
        }

        tipText.setText(tip);
    }
}
