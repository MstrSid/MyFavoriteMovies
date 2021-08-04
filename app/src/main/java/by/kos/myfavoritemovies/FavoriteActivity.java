package by.kos.myfavoritemovies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import by.kos.myfavoritemovies.databinding.ActivityFavoriteBinding;

public class FavoriteActivity extends AppCompatActivity {
    private ActivityFavoriteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}