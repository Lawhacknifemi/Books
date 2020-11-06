package com.example.books;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ProgressBar mLoadingProgress;
    TextView tvResult;
    private RecyclerView rvBook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading);
        rvBook = findViewById(R.id.rvBook);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvBook.setLayoutManager(linearLayoutManager);
        Intent intent = getIntent();
        String query = intent.getStringExtra("Query");
        URL bookUrl;

        try {
            if (query == null || query.isEmpty()){
                bookUrl = ApiUtil.buildUrl("android");
            }else{
                bookUrl = new URL(query);

            }
            new BooksQueryTask().execute(bookUrl);

        }
        catch (Exception e) {
            Log.d("error", e.getMessage());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_list_menu,menu);
       final MenuItem searchItem = menu.findItem(R.id.actionSearch);
       final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        ArrayList<String> recentList = SpUtil.getQueryList(getApplicationContext());
        int itemNum = recentList.size();
        MenuItem recentMenu ;
        for (int i = 0; i < itemNum; i++) {
             recentMenu = menu.add(Menu.NONE,i,Menu.NONE,recentList.get(i));

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_advance_search:
                Intent advanceSearch = new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(advanceSearch);
                return true;
            default:
                int position =item.getItemId() +1;
                String preferenceName = SpUtil.QUERY + String.valueOf(position);
                String query = SpUtil.getPrefString(getApplicationContext(),preferenceName);
                String[] prefsParam = query.split("\\,");
                String[] queryParam = new String[4];
                for (int i = 0; i < queryParam.length; i++) {
                    queryParam[i] = prefsParam[i];
                    
                }
                URL bookURL = ApiUtil.buildUrl(
                        (queryParam[0] == null)?"": queryParam[0],
                        (queryParam[1] == null)?"": queryParam[1],
                        (queryParam[2] == null)?"": queryParam[2],
                        (queryParam[3] == null)?"": queryParam[3]
                );


                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            URL bookURL = ApiUtil.buildUrl(query);
            new BooksQueryTask().execute(bookURL);
        } catch (Exception e) {
           Log.d("error",e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    public class BooksQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String result = null;
            try {
                result = ApiUtil.getJson(searchURL);
            }
            catch (IOException e) {
                Log.e("Error", e.getMessage());
            }
            return result;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String result) {

            TextView tvError = (TextView) findViewById(R.id.tv_error);
            mLoadingProgress.setVisibility(View.INVISIBLE);

            if (result == null) {
               rvBook.setVisibility(View.INVISIBLE);
                tvError.setVisibility(View.VISIBLE);
            }
            else {
                rvBook.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.INVISIBLE);
                ArrayList<Book> books = ApiUtil.getBooksFromJson(result);
                String resultString = "";

                BooksAdapter adapter = new BooksAdapter(books);
                rvBook.setAdapter(adapter);
            }

        }

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            mLoadingProgress.setVisibility(View.VISIBLE);
        }
    }
}