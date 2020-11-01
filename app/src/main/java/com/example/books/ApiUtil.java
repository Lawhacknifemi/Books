package com.example.books;

import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;

import static java.util.Objects.isNull;

public class ApiUtil {
    private ApiUtil(){}

    public static final String BASE_API_URL =
            "https://www.googleapis.com/books/v1/volumes";
    public static final String QUERY_PARAMETER_KEY = "q";
    public static final String KEY = "key";
    public static final String API_KEY = "AIzaSyCK9Z5fuQBLM0RXV58u1Wmkt9zznb0269c";
    public static final String TITLE = "intitle";
    public static final String AUTHORS = "inauthor";
    public static final String PUBLISHER = "inpublisher";
    public static final String ISBN = "isbn";

    public static URL buildUrl(String title) {

        URL url = null;
        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY, title)
                .appendQueryParameter(KEY, API_KEY)
                .build();
        try {
            url = new URL(uri.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrl(String title, String author, String publisher,String isbn){
        URL url = null;
    StringBuilder sb = new StringBuilder();
    if(!title.isEmpty()) sb.append(TITLE + title + "+");
    if(!author.isEmpty()) sb.append(AUTHORS + author + "+");
    if (!publisher.isEmpty()) sb.append(PUBLISHER + publisher + "+");
    if (!publisher.isEmpty()) sb.append(ISBN + isbn + "+");
    sb.setLength(sb.length() - 1);
    String query = sb.toString();
    Uri uri = Uri.parse(BASE_API_URL).buildUpon()
            .appendQueryParameter(QUERY_PARAMETER_KEY,query)
            .appendQueryParameter(KEY,API_KEY)
            .build();
    try{
        url = new URL(uri.toString());

    } catch (MalformedURLException e) {
        e.printStackTrace();
    }
    return url;
    }


    public static String getJson(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            InputStream stream = connection.getInputStream();
            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A");
            boolean hasData = scanner.hasNext();
            if (hasData) {
                return scanner.next();
            } else {
                return null;
            }
        }
        catch (Exception e){
            Log.d("Error", e.toString());
            return null;
        }
        finally {
            connection.disconnect();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<Book> getBooksFromJson (String json){
        final String ID = "id";
        final String TITLE = "title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String PUBLISHER = "publisher";
        final String PUBLISHED_DATE = "publishedDate";
        final String ITEMS = "items";
        final String VOLUME_INFO = "volumeInfo";
        final String DESCRIPTION = "description";
        final String IMAGELINKS = "imageLinks";
        final  String THUMBNAILS = "thumbnail";

        ArrayList<Book> books = new ArrayList<Book>();
        try{
            JSONObject jsonBooks = new JSONObject(json);
            JSONArray arrayBooks =  jsonBooks.getJSONArray(ITEMS);
            int numberOfBooks = arrayBooks.length();
            for (int i = 0; i < numberOfBooks; i++) {
                 JSONObject bookJSON = arrayBooks.getJSONObject(i);
                 JSONObject volumeInfoJSOn = bookJSON.getJSONObject(VOLUME_INFO);
                 JSONObject ImageLinkJson = volumeInfoJSOn.getJSONObject(IMAGELINKS);

                 int authorNum = volumeInfoJSOn.getJSONArray(AUTHORS).length();
                 String[] authors = new String[authorNum];
                for (int j = 0; j < authorNum; j++) {
                     authors[j] = volumeInfoJSOn.getJSONArray(AUTHORS).get(j).toString();
                }
                Book book = new Book(
                        bookJSON.getString(ID),
                        volumeInfoJSOn.getString(TITLE),
                        (volumeInfoJSOn.isNull(SUBTITLE)?"":volumeInfoJSOn.getString(SUBTITLE)),
                        authors,
                        volumeInfoJSOn.getString(PUBLISHER),
                        volumeInfoJSOn.getString(PUBLISHED_DATE),
                        (volumeInfoJSOn.isNull(DESCRIPTION)?"":volumeInfoJSOn.getString(DESCRIPTION)),
                        ImageLinkJson.getString(THUMBNAILS)
                        );

                books.add(book);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return books;
    }
}
