package com.example.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.books.databinding.ActivityBookDetalisBinding;

public class BookDetalisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detalis);

        Book book = getIntent().getParcelableExtra("Book");
        ActivityBookDetalisBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_book_detalis);
        binding.setBook(book);
    }
}