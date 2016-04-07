package in.good_work.getmoreinfo_starchenkoalex.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

import in.good_work.getmoreinfo_starchenkoalex.R;

/**
 * Created by Alex on 07.04.2016.
 */
public class InfoThread extends AsyncTask<String, Void, String> {
    private ProgressDialog pDialog;
    private String catalog_url;
    private ListView infoLv;
    private ProductAdapter adapter;
    private Activity context;
    private Elements content;
    private List<Product> products;

    public InfoThread(String catalog_url, Activity context, List<Product> products, ListView infoLv, ProductAdapter adapter) {
        this.context = context;
        this.products = products;
        this.catalog_url = catalog_url;
        this.infoLv = infoLv;
        this.adapter = adapter;

        this.pDialog = new ProgressDialog(context);
        this.pDialog.setMessage(context.getString(R.string.laod_image_progres));
        this.pDialog.show();
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
        Parcelable state = infoLv.onSaveInstanceState();

        infoLv.setAdapter(adapter);
        this.pDialog.hide();

        infoLv.onRestoreInstanceState(state);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
        r.play();
    }
}
