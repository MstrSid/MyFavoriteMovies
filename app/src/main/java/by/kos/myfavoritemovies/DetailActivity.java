package by.kos.myfavoritemovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import by.kos.myfavoritemovies.adapters.ReviewAdapter;
import by.kos.myfavoritemovies.adapters.TrailerAdapter;
import by.kos.myfavoritemovies.data.FavoriteMovie;
import by.kos.myfavoritemovies.data.Movie;
import by.kos.myfavoritemovies.data.MovieDatabase;
import by.kos.myfavoritemovies.data.Review;
import by.kos.myfavoritemovies.data.Trailer;
import by.kos.myfavoritemovies.databinding.ActivityDetailBinding;
import by.kos.myfavoritemovies.utils.JSONUtils;
import by.kos.myfavoritemovies.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private int id;
    private MainViewModel viewModel;
    private Movie movie;
    private FavoriteMovie favoriteMovie;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private ArrayList<Review> reviews;
    private ArrayList<Trailer> trailers;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        reviewAdapter = new ReviewAdapter();
        trailerAdapter = new TrailerAdapter();

        Intent receiveMovieIntent = getIntent();
        if (receiveMovieIntent != null && receiveMovieIntent.hasExtra("id")) {
            id = receiveMovieIntent.getIntExtra("id", -1);
        } else finish();

        movie = viewModel.getMovieById(id);

        Picasso.get().load(movie.getBigPosterPath()).into(binding.ivBigPoster);

        binding.tvTitle.setText(movie.getTitle());
        binding.tvOriginalTitle.setText(movie.getOriginalTitle());
        binding.tvRating.setText(Double.toString(movie.getVoteAverage()));
        binding.tvReleaseDate.setText(movie.getReleaseDate());
        binding.tvOverview.setText(movie.getOverview());
        setFavorite();

        binding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReviews.setAdapter(reviewAdapter);

        binding.rvTrailers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTrailers.setAdapter(trailerAdapter);

        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId());
        reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        reviewAdapter.setReviews(reviews);

        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForTrailers(movie.getId());
        trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
        trailerAdapter.setTrailers(trailers);

        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent playYoutube = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(playYoutube);
            }
        });

        binding.fabFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favoriteMovie == null) {
                    viewModel.insertFavoriteMovie(new FavoriteMovie(movie));
                } else {
                    viewModel.deleteFavoriteMovie(favoriteMovie);
                }
                setFavorite();
            }
        });
    }

    private void setFavorite() {
        favoriteMovie = viewModel.getFavoriteMovieById(id);
        if (favoriteMovie == null) {
            binding.fabFavorites.setImageResource(R.drawable.ic_not_favorites);
        } else {
            binding.fabFavorites.setImageResource(R.drawable.ic_is_favorites);
        }
    }
}