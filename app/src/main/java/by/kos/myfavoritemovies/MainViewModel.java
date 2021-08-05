package by.kos.myfavoritemovies;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import by.kos.myfavoritemovies.data.Movie;
import by.kos.myfavoritemovies.data.MovieDatabase;

public class MainViewModel extends AndroidViewModel {
    private static MovieDatabase database;
    private LiveData<List<Movie>> movies;

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        movies = database.movieDao().getAllMovies();
    }

    public Movie getMovieById(int id) {
        try {
            return new GetMovieTask().execute(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAllMovies() {
        new DeleteAllMoviesTask().execute();
    }

    public void deleteMovie(Movie movie) {
        new DeleteMovieTask().execute(movie);
    }

    public void insertMovie(Movie movie) {
        new InsertMovieTask().execute(movie);
    }

    private static class GetMovieTask extends AsyncTask<Integer, Void, Movie> {
        @Override
        protected Movie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.movieDao().getMovieById(integers[0]);
            }
            return null;
        }
    }

    private static class DeleteMovieTask extends AsyncTask<Movie, Void, Void> {
        @Override
        protected Void doInBackground(Movie... movies) {
            database.movieDao().deleteMovie(movies[0]);
            return null;
        }
    }

    private static class InsertMovieTask extends AsyncTask<Movie, Void, Void> {
        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().insertMovie(movies[0]);
            }
            return null;
        }
    }

    private static class DeleteAllMoviesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            database.movieDao().deleteAllMovies();
            return null;
        }
    }
}
