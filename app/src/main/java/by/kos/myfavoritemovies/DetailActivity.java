package by.kos.myfavoritemovies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import by.kos.myfavoritemovies.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}