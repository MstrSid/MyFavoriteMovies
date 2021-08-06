package by.kos.myfavoritemovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import by.kos.myfavoritemovies.data.FavoriteMovie;
import by.kos.myfavoritemovies.data.Movie;
import by.kos.myfavoritemovies.databinding.ActivityFavoriteBinding;
import by.kos.myfavoritemovies.adapters.MovieAdapter;

public class FavoriteActivity extends AppCompatActivity {
    private ActivityFavoriteBinding binding;
    private MovieAdapter movieAdapter;
    private MainViewModel viewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.item_favorite);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item_main:
                Intent openMainIntent = new Intent(this, MainActivity.class);
                startActivity(openMainIntent);
                break;
            case R.id.item_favorite:
                Intent openFavoriteIntent = new Intent(this, FavoriteActivity.class);
                startActivity(openFavoriteIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        movieAdapter = new MovieAdapter();
        binding.rvFavoriteMovies.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvFavoriteMovies.setAdapter(movieAdapter);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        LiveData<List<FavoriteMovie>> favoriteMovies = viewModel.getFavoriteMovies();
        favoriteMovies.observe(this, new Observer<List<FavoriteMovie>>() {
            @Override
            public void onChanged(List<FavoriteMovie> favoriteMovies) {
                List<Movie> movies = new ArrayList<>();
                if (favoriteMovies != null) {
                    movies.addAll(favoriteMovies);
                    movieAdapter.setMovies(movies);
                }
            }
        });

        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Intent openDetailsIntent = new Intent(FavoriteActivity.this, DetailActivity.class);
                Movie movie = movieAdapter.getMovies().get(position);
                openDetailsIntent.putExtra("id", movie.getId());
                startActivity(openDetailsIntent);
            }
        });
    }
}