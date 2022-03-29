package com.lwitts.iiifgallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button button;
    String url = "";
    private RecyclerView recyclerView;
    private ArrayList<ImageData> imageDataArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createThread();
            }
        });

    }

    private void createThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //do in background!
                url = getSiteString("https://iiif.bodleian.ox.ac.uk/iiif/manifest/441db95d-cdff-472e-bb2d-b46f043db82d.json");
                //url = getSiteString("https://api.bl.uk/metadata/iiif/ark:/81055/vdc_100056663076.0x000001/manifest.json?manifest=https://api.bl.uk/metadata/iiif/ark:/81055/vdc_100056663076.0x000001/manifest.json");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            setGalleryList(url);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }


    private String getSiteString(String site) {
        String stream = "";
        try {
            URL url = new URL(site);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String inputLine;
                while ((inputLine = rd.readLine()) != null) {
                    sb.append(inputLine);
                    sb.append("\n");
                }
                stream = sb.toString();
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }

    private void setGalleryList(String url) throws ParseException {

        imageDataArrayList = new ArrayList<>();
        JSONParser jsonParse = new JSONParser();
        JSONObject jObj = (JSONObject) jsonParse.parse(url);

        JSONArray jsonArray = (JSONArray) jObj.get("sequences");
        for (Object o : jsonArray) {
            JSONObject result = (JSONObject) o;
            JSONArray canvases = (JSONArray) result.get("canvases");

            for (Object cnv : canvases) {
                JSONObject canvas = (JSONObject) cnv;
                String label = (String) canvas.get("label");
                System.out.println(label);
                JSONArray images = (JSONArray) canvas.get("images");

                for (Object img : images) {
                    JSONObject image = (JSONObject) img;
                    JSONObject resources = (JSONObject) image.get("resource");

                    String itemId = (String) resources.get("@id");
                    Long height = (Long) canvas.get("height");
                    String heightStr = Long.toString(height);
                    Long width = (Long) canvas.get("width");
                    String widthStr = Long.toString(width);

                    //checking to see if it has a .jpg extension, might need more? Or be more generic?
                   // if (resId.getPath().contains(".jpg")) {
                    if(itemId.contains(".jpg")){

                        imageDataArrayList.add(new ImageData(label, itemId));
                    } else {
                        String imageUrl = createUrl(itemId, heightStr, widthStr);
                        //System.out.println(imageUrl);
                        imageDataArrayList.add(new ImageData(label, imageUrl));
                    }
                }
            }

            recyclerView = findViewById(R.id.recycler_view_image_id);

            ImageViewAdapter adapter = new ImageViewAdapter(imageDataArrayList, this);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

        }

    }

    public static String createUrl(String id, String height, String width) {
        //generic URL to get the image .jpg
        return id + "/full/" + height + "," + width + "/0/" + "default.jpg";
    }

}