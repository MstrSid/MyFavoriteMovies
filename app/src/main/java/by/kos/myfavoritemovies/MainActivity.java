package by.kos.myfavoritemovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import by.kos.myfavoritemovies.data.Movie;
import by.kos.myfavoritemovies.databinding.ActivityMainBinding;
import by.kos.myfavoritemovies.utils.JSONUtils;
import by.kos.myfavoritemovies.adapters.MovieAdapter;
import by.kos.myfavoritemovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {
    private ActivityMainBinding binding;
    private MovieAdapter movieAdapter;
    private MainViewModel movieViewModel;
    private final static int LOADER_ID = 1;
    private LoaderManager loaderManager;
    private static int page = 1;
    private static boolean isLoading = false;
    private static int sortCriteria;
    private static String lang;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.item_main);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
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

    private int getColumnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return width / 185 > 2 ? width / 185 : 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        loaderManager = LoaderManager.getInstance(this);
        movieViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        movieAdapter = new MovieAdapter();
        lang = Locale.getDefault().getLanguage();
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
                if (!isLoading) {
                    downloadData(sortCriteria, page);
                }
            }
        });
        binding.rvMovies.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        binding.tvPopularity.setTextColor(getResources().getColor(R.color.gold));
        binding.rvMovies.setAdapter(movieAdapter);
        binding.switchCategory.setChecked(true);
        binding.switchCategory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                page = 1;
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
                if (page == 1) {
                    movieAdapter.setMovies(movies);
                }
            }
        });

    }

    private void setMethodOfSort(boolean isTopRated) {
        if (isTopRated) {
            binding.tvTopRated.setTextColor(getResources().getColor(R.color.gold));
            binding.tvPopularity.setTextColor(getResources().getColor(R.color.primaryTextColor));
            sortCriteria = NetworkUtils.TOP_RATED;
        } else {
            sortCriteria = NetworkUtils.POPULARITY;
            binding.tvPopularity.setTextColor(getResources().getColor(R.color.gold));
            binding.tvTopRated.setTextColor(getResources().getColor(R.color.primaryTextColor));
        }
        downloadData(sortCriteria, page);

    }

    private void downloadData(int sortCriteria, int page) {
        URL url = NetworkUtils.buildUrl(sortCriteria, page, lang);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID, bundle, this);
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, args);
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.OnStartLoadingListener() {
            @Override
            public void onStartLoading() {
                binding.pbLoading.setVisibility(View.VISIBLE);
                isLoading = true;
            }
        });
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(data);
        if (movies != null && !movies.isEmpty()) {
            if (page == 1) {
                movieViewModel.deleteAllMovies();
                movieAdapter.clear();
            }
            for (Movie movie : movies) {
                movieViewModel.insertMovie(movie);
            }
            movieAdapter.addMovies(movies);
            page++;
        }
        isLoading = false;
        binding.pbLoading.setVisibility(View.INVISIBLE);
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}