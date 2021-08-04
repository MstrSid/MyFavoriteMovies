package by.kos.myfavoritemovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import by.kos.myfavoritemovies.data.Movie;
import by.kos.myfavoritemovies.databinding.ActivityMainBinding;
import by.kos.myfavoritemovies.utils.JSONUtils;
import by.kos.myfavoritemovies.utils.MovieAdapter;
import by.kos.myfavoritemovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        movieAdapter = new MovieAdapter();
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                //Intent openDetails = new Intent(MainActivity.this, DetailActivity.class);
                //startActivity(openDetails);
                Toast.makeText(MainActivity.this, position + "", Toast.LENGTH_SHORT).show();
            }
        });
        movieAdapter.setOnGetEndListener(new MovieAdapter.OnGetEndListener() {
            @Override
            public void onGetEnd() {
                Toast.makeText(MainActivity.this, "End list", Toast.LENGTH_SHORT).show();
            }
        });
        binding.rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        sort(NetworkUtils.POPULARITY);
        binding.tvPopularity.setTextColor(getResources().getColor(R.color.gold));
        binding.rvMovies.setAdapter(movieAdapter);

        binding.switchCategory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int sortCriteria;
                if (isChecked) {
                    binding.tvTopRated.setTextColor(getResources().getColor(R.color.gold));
                    binding.tvPopularity.setTextColor(getResources().getColor(R.color.primaryTextColor));
                    sortCriteria = NetworkUtils.TOP_RATED;
                } else {
                    sortCriteria = NetworkUtils.POPULARITY;
                    binding.tvPopularity.setTextColor(getResources().getColor(R.color.gold));
                    binding.tvTopRated.setTextColor(getResources().getColor(R.color.primaryTextColor));
                }
                sort(sortCriteria);
            }
        });

        binding.tvPopularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort(NetworkUtils.POPULARITY);
                binding.switchCategory.setChecked(false);
                binding.tvPopularity.setTextColor(getResources().getColor(R.color.gold));
                binding.tvTopRated.setTextColor(getResources().getColor(R.color.primaryTextColor));
            }
        });


        binding.tvTopRated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort(NetworkUtils.TOP_RATED);
                binding.switchCategory.setChecked(true);
                binding.tvTopRated.setTextColor(getResources().getColor(R.color.gold));
                binding.tvPopularity.setTextColor(getResources().getColor(R.color.primaryTextColor));
            }
        });

    }

    private void sort(int sortCriteria) {
        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(sortCriteria, 1);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        movieAdapter.setMovies(movies);
    }
}