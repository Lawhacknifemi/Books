package com.example.books;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final EditText etTitle = findViewById(R.id.etTitle);
        final EditText etAuthor = findViewById(R.id.etAuthor);
        final EditText etPublisher = findViewById(R.id.etPublisher);
        final EditText etISBN = findViewById(R.id.etISBN);
        final Button btnSearch = findViewById(R.id.btnSearch);



        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString().trim();
                String authors = etAuthor.getText().toString().trim();
                String publisher = etPublisher.getText().toString().trim();
                String ISBN = etISBN.getText().toString().trim();
                if (title.isEmpty() && authors.isEmpty() & publisher.isEmpty() && ISBN.isEmpty()){
                    String message = getText(R.string.no_search_data).toString();
                    Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                else {
                    URL queryURL = ApiUtil.buildUrl(title,authors,publisher,ISBN);
                    //sharedPrefrence
                    Context context = getApplicationContext();
                    int position = SpUtil.getPrefInt(context,SpUtil.POSITION);
                    if(position == 0 || position== 5){
                        position = 1;
                    }else{
                        position++;
                    }
                    String key = SpUtil.QUERY + String.valueOf(position);
                    String value = title + "," + authors + "," + publisher + "," + ISBN;
                    SpUtil.setPreferenceString(context,key,value);
                    SpUtil.setPreferenceInt(context,SpUtil.POSITION,position);

                    Intent intent = new Intent(getApplicationContext(),BookDetalisActivity.class);
                    intent.putExtra("Query",queryURL);
                    startActivity(intent);
                }

            }
        });

    }
}