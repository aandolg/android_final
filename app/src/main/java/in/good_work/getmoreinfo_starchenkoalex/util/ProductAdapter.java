package in.good_work.getmoreinfo_starchenkoalex.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.good_work.getmoreinfo_starchenkoalex.R;

/**
 * Created by Alex on 07.04.2016.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    private List<Product> products;

    public ProductAdapter(Context context, List<Product> products) {
        super(context, R.layout.item_info, products);

        this.products = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_info, null);
        }


        ImageView img_ad = (ImageView) convertView.findViewById(R.id.product_img_item_adapter);

        Picasso.with(getContext()).load(product.src_image).into(img_ad);
        ((TextView) convertView.findViewById(R.id.product_name_item_adapter))
                .setText(product.text);
        ((TextView) convertView.findViewById(R.id.price_name_item_adapter))
                .setText(product.price);
        return convertView;
    }
}
