package by.kos.myfavoritemovies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import by.kos.myfavoritemovies.data.Movie;
import by.kos.myfavoritemovies.databinding.ActivityMainBinding;
import by.kos.myfavoritemovies.utils.JSONUtils;
import by.kos.myfavoritemovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(NetworkUtils.POPULARITY, 1);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        for(Movie movie : movies){
            Log.d("resultMovies", movie.getTitle());
        }
    }
}