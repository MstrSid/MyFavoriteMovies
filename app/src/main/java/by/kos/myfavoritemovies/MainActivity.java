package by.kos.myfavoritemovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import java.util.List;

import by.kos.myfavoritemovies.data.Movie;
import by.kos.myfavoritemovies.databinding.ActivityMainBinding;
import by.kos.myfavoritemovies.utils.JSONUtils;
import by.kos.myfavoritemovies.utils.MovieAdapter;
import by.kos.myfavoritemovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MovieAdapter movieAdapter;
    private MainViewModel movieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        movieViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        movieAdapter = new MovieAdapter();
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Intent openDetailsIntent = new Intent(MainActivity.this, DetailActivity.class);
                Movie movie = movieAdapter.getMovies().get(position);
                openDetailsIntent.putExtra("id", movie.getId());
                startActivity(openDetailsIntent);
            }
        });
        movieAdapter.setOnGetEndListener(new MovieAdapter.OnGetEndListener() {
            @Override
            public void onGetEnd() {
                //Toast.makeText(MainActivity.this, "End list", Toast.LENGTH_SHORT).show();
            }
        });
        binding.rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        binding.tvPopularity.setTextColor(getResources().getColor(R.color.gold));
        binding.rvMovies.setAdapter(movieAdapter);
        binding.switchCategory.setChecked(true);
        binding.switchCategory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setMethodOfSort(isChecked);
            }
        });
        binding.switchCategory.setChecked(false);

        binding.tvPopularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMethodOfSort(false);
                binding.switchCategory.setChecked(false);
            }
        });


        binding.tvTopRated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMethodOfSort(true);
                binding.switchCategory.setChecked(true);
            }
        });

        LiveData<List<Movie>> moviesFromLiveData = movieViewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                movieAdapter.setMovies(movies);
            }
        });

    }

    private void setMethodOfSort(boolean isTopRated){
        int sortCriteria;
        if (isTopRated) {
            binding.tvTopRated.setTextColor(getResources().getColor(R.color.gold));
            binding.tvPopularity.setTextColor(getResources().getColor(R.color.primaryTextColor));
            sortCriteria = NetworkUtils.TOP_RATED;
        } else {
            sortCriteria = NetworkUtils.POPULARITY;
            binding.tvPopularity.setTextColor(getResources().getColor(R.color.gold));
            binding.tvTopRated.setTextColor(getResources().getColor(R.color.primaryTextColor));
        }
        downloadData(sortCriteria, 1);

    }

    private void downloadData(int sortCriteria, int page) {
        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(sortCriteria, 1);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        if (movies != null && !movies.isEmpty()) {
            movieViewModel.deleteAllMovies();
            for (Movie movie : movies) {
                movieViewModel.insertMovie(movie);
            }
        }
    }
}