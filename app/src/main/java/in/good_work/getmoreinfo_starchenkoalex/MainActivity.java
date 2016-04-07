package in.good_work.getmoreinfo_starchenkoalex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final List<Product> products = new ArrayList<Product>();
    public Elements content;
    //    private ArrayAdapter<String> adapter;
    private ArrayAdapter<Product> adapter = null;
    private ListView infoLv;
    private RelativeLayout mainRl;
    private Button nextBt;
    private Button refreshBtn = null;
    private String catalog_url = "http://good-work.in/catalog/ikea";
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.title_main_activity);

        nextBt = (Button) findViewById(R.id.next_bt);
        nextBt.setOnClickListener(this);
        if (this.isNetworkConnected()) {
            if (refreshBtn == null) {
                this.init();
            }

        } else {

            refreshBtn = new Button(this);

            nextBt.setVisibility(View.INVISIBLE);

            RelativeLayout.LayoutParams centerLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            centerLp.addRule(RelativeLayout.CENTER_IN_PARENT);

            mainRl = (RelativeLayout) findViewById(R.id.main_layout);
            refreshBtn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            refreshBtn.setText(R.string.refresh_bt);
            refreshBtn.setId(Integer.valueOf(1));

            refreshBtn.setLayoutParams(centerLp);
            mainRl.addView(refreshBtn);
            refreshBtn.setOnClickListener(this);
            this.showToast(getString(R.string.errors_internet));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 1:                             //refresh button click
                if (this.isNetworkConnected()) {
                    this.init();
                } else {
                    this.showToast(getString(R.string.errors_internet));
                }
                break;
            case R.id.next_bt:
//                new InfoThread().execute();
                this.page++;
                new InfoThread(catalog_url + "?page=" + this.page).execute();
                Parcelable state = infoLv.onSaveInstanceState();

                adapter.notifyDataSetChanged();

                infoLv.onRestoreInstanceState(state);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void showToast(String text) {
        if (text.length() > 0) {
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void init() {
        this.infoLv = (ListView) findViewById(R.id.info_lv);
        this.infoLv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        new InfoThread(catalog_url).execute();
        this.adapter = new ProductAdapter(this);
        if (this.refreshBtn != null)
            this.refreshBtn.setVisibility(View.INVISIBLE);
        this.nextBt.setVisibility(View.VISIBLE);
        infoLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ProductActivity.class);
                intent.putExtra("product_name", products.get((int) id).text);
                intent.putExtra("product_price", products.get((int) id).price);
                intent.putExtra("product_annotation", products.get((int) id).annotation);
                intent.putExtra("product_src_image", products.get((int) id).src_image);
                startActivity(intent);
            }
        });

    }

    private static class Product {
        public final String price;
        public final String text;
        public final String link;
        public final String annotation;
        public final String src_image;

        public Product(String price, String text, String link, String annotation, String src_image) {
            this.price = price;
            this.text = text;
            this.link = link;
            this.annotation = annotation;
            this.src_image = src_image;
        }
    }

    public class InfoThread extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;
        private String catalog_url;

        public InfoThread(String catalog_url) {
            this.pDialog = new ProgressDialog(MainActivity.this);
            this.pDialog.setMessage(getString(R.string.laod_image_progres));
            this.pDialog.show();
            this.catalog_url = catalog_url;
        }

        @Override
        protected String doInBackground(String... params) {
            Document html;
            try {
                html = Jsoup.connect(this.catalog_url).get();
                content = html.select(".product");
                for (Element contents : content) {

                    String price = contents.select(".price").text();
                    String name = contents.select("a").text();
                    String href = contents.select("a").attr("href");
                    String img_link = contents.select("img").attr("src");
                    String annotation = contents.select(".annotation").text();
                    products.add(new Product(price, name, href, annotation, img_link));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            infoLv.setAdapter(adapter);
            this.pDialog.hide();
        }
    }

    private class ProductAdapter extends ArrayAdapter<Product> {

        public ProductAdapter(Context context) {
            super(context, R.layout.item_info, products);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Product product = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_info, null);
            }


            ImageView img_ad = (ImageView) convertView.findViewById(R.id.product_img_item_adapter);

//            new LoadImage(img_ad).execute(product.src_image);
            Picasso.with(getContext()).load(product.src_image).into(img_ad);
            ((TextView) convertView.findViewById(R.id.product_name_item_adapter))
                    .setText(product.text);
            ((TextView) convertView.findViewById(R.id.price_name_item_adapter))
                    .setText(product.price);
            return convertView;
        }


        private class LoadImage extends AsyncTask<String, String, Bitmap> {
            Bitmap bitmap = null;
            ProgressDialog pDialog = null;
            ImageView imageView = null;

            public LoadImage(ImageView imageView) {
//            super();
                this.imageView = imageView;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
               /* pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Loading Image ....");
                pDialog.show();*/

            }

            protected Bitmap doInBackground(String... args) {
                try {
                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            protected void onPostExecute(Bitmap image) {

                if (image != null) {
                    this.imageView.setImageBitmap(image);
                    pDialog.dismiss();

                } else {

                    pDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

}
