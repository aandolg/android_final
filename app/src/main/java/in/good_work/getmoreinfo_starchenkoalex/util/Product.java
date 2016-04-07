package in.good_work.getmoreinfo_starchenkoalex.util;

/**
 * Created by Alex on 07.04.2016.
 */
public class Product {
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
