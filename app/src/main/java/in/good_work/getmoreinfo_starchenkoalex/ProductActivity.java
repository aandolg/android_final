package in.good_work.getmoreinfo_starchenkoalex;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Intent intent = getIntent();
        String product_name = intent.getStringExtra("product_name");
        String product_price = intent.getStringExtra("product_price");
        String product_annotation = intent.getStringExtra("product_annotation");
        String product_src_image = intent.getStringExtra("product_src_image");
        setTitle(product_name);

        ImageView img = null;
        if ((img = (ImageView) findViewById(R.id.product_detail_imv)) != null)
            Picasso.with(ProductActivity.this).load(product_src_image).into(img);

        ((TextView) findViewById(R.id.product_price_deteil_tv)).setText(product_name + " " + product_price);
        ((TextView) findViewById(R.id.product_annotation_deteil_tv)).setText(product_annotation);
    }


}
