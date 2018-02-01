package biz.channelit.graph.hin.google;

import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author saxman
 */
public class NearbyFromGoogle {
    private static final String LOG_TAG = "ExampleApp";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_SEARCH = "/search";
    private static final String TYPE_NEARBY = "/nearbysearch";

    private static final String OUT_JSON = "/json";

    @Value("${google.api.key}")
    private static final String API_KEY = "AIzaSyDREL3s4ZgopLqIp55xFmH3LFlXf58u67w";

    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=40.7630882,-73.9663085&rankby=distance&types=food&key=AIzaSyDREL3s4ZgopLqIp55xFmH3LFlXf58u67w

    private static Map<String, Integer> searchNearby(double lat, double lng, int radius) {

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE)
                    .append(TYPE_NEARBY)
                    .append(OUT_JSON)
                    .append("?sensor=false")
                    .append("&key=" + API_KEY)
                    .append("&location=")
                    .append(String.valueOf(lat))
                    .append(",")
                    .append(String.valueOf(lng))
                    .append("&radius=")
                    .append(String.valueOf(radius));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (IOException e) {
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }


        // Create a JSON object hierarchy from the results
        JSONObject jsonObj = new JSONObject(jsonResults.toString());
        JSONArray results = jsonObj.getJSONArray("results");
        results.forEach(j -> {
            JSONArray types = (JSONArray) ((JSONObject) j).get("types");
            System.out.println(types.toList());
        });


        return null;
    }

    public static void findLocations() {
        searchNearby(40.718906402587891, -74.006759643554687, 50);
    }
}
