package by.kos.myfavoritemovies.utils;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class NetworkUtils {
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String BASE_TRAILERS_URL = "https://api.themoviedb.org/3/movie/%s/videos";
    private static final String BASE_REVIEWS_URL = "https://api.themoviedb.org/3/movie/%s/reviews";
    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";

    private static final String API_KEY = "1969470c7f2585b34d8063c6fb2aa3a7";
    private static final String LANGUAGE_VALUE = "ru-RU";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";

    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;

    private static URL buildUrlToTrailers(int id){
        Uri uri = Uri.parse(String.format(BASE_TRAILERS_URL, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static URL buildUrlToReviews(int id){
        Uri uri = Uri.parse(String.format(BASE_REVIEWS_URL, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                /*.appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE)*/
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static URL buildUrl(int sortBy, int page) {
        String sortMethod;
        URL result = null;

        if (sortBy == POPULARITY) {
            sortMethod = SORT_BY_POPULARITY;
        } else sortMethod = SORT_BY_TOP_RATED;

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE)
                .appendQueryParameter(PARAMS_SORT_BY, sortMethod)
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page))
                .build();

        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject getJSONFromNetwork(int sortBy, int page){
        JSONObject result = null;
        URL url = buildUrl(sortBy, page);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject getJSONForTrailers(int id){
        JSONObject result = null;
        URL url = buildUrlToTrailers(id);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static JSONObject getJSONForReviews(int id){
        JSONObject result = null;
        URL url = buildUrlToReviews(id);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static class JSONLoadTask extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject result = null;
            if (urls == null || urls.length == 0) {
                return result;
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) urls[0].openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder builder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null){
                    builder.append(line);
                    line = bufferedReader.readLine();
                }
                result = new JSONObject(builder.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }
    }

}
