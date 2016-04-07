package in.good_work.getmoreinfo_starchenkoalex;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import in.good_work.getmoreinfo_starchenkoalex.util.InfoThread;
import in.good_work.getmoreinfo_starchenkoalex.util.Product;
import in.good_work.getmoreinfo_starchenkoalex.util.ProductAdapter;

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
                new InfoThread(catalog_url + "?page=" + this.page, MainActivity.this, products, infoLv, (ProductAdapter) adapter).execute();

                adapter.notifyDataSetChanged();


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

        this.adapter = new ProductAdapter(this, products);
        new InfoThread(catalog_url, MainActivity.this, products, infoLv, (ProductAdapter) adapter).execute();
//        new InfoThread(catalog_url + "?page=" + this.page,MainActivity.this,products,infoLv,(ProductAdapter)adapter).execute();

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

}
