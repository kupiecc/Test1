package net.jackapp.auctionchecker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jacekkupczak on 18.05.16.
 */
public class ImageLoader {
    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;

    private Map<ImageView, String> imageViews =
            Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

    ExecutorService executorService;

    public ImageLoader(Context context) {

        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);

    }

    final int stub_id = R.drawable.no_img;

    public void displayImage(String url, ImageView imageView, ProgressBar progressBar) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            System.out.println("Bitmap from cache");
            progressBar.setVisibility(View.GONE);
            imageView.setImageBitmap(bitmap);
        } else {
            System.out.println("Bitmap from url");
            progressBar.setVisibility(View.VISIBLE);
            new DownloadImageTask(imageView, progressBar).execute(url);
            queuePhoto(url, imageView);
            imageView.setImageResource(stub_id);
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url) {

        File f = fileCache.getFile(url);
        Bitmap b = decodeFile(f);
        if (b != null)
            return b;

        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);

            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.copyStream(is, os);
            os.close();
            BitmapFactory.Options bfOptions = new BitmapFactory.Options();
            bfOptions.inJustDecodeBounds = false;
            bfOptions.inSampleSize = 3;
            bitmap = BitmapFactory.decodeStream(is, null, bfOptions);
            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();
            return null;
        }

    }

    private Bitmap decodeFile(File f) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        return BitmapFactory.decodeFile(f.getAbsolutePath());
    }

    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {

        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad)) {
                return;
            }

            Bitmap bmp = getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);

            if (imageViewReused(photoToLoad)) {
                return;
            }

            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        return tag == null || !tag.equals(photoToLoad.url);
    }

    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;

            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
            } else {
                photoToLoad.imageView.setImageResource(stub_id);
            }
        }
    }

    public void clearCahce() {
        memoryCache.clear();
        fileCache.clear();
    }

    private Bitmap loadImage(String url) {
        File fileFromCache = fileCache.getFile(url);

        URL picUrl = null;
        Bitmap bitmap = null;

        Bitmap b = BitmapFactory.decodeFile(fileFromCache.getAbsolutePath());
        if (b != null) {
            return b;
        }

        try {
            picUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) picUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);

            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(fileFromCache);
            Utils.copyStream(is, os);
            os.close();
            BitmapFactory.Options bfOptions = new BitmapFactory.Options();
            bfOptions.inJustDecodeBounds = false;
            bfOptions.inSampleSize = 3;
            bitmap = BitmapFactory.decodeFile(fileFromCache.getAbsolutePath(), bfOptions);

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> imgRef;
        private final WeakReference<ProgressBar> pbRef;
        private PhotoToLoad photoToLoad;

        public DownloadImageTask(ImageView imageView, ProgressBar progressBar) {
            imgRef = new WeakReference<ImageView>(imageView);
            pbRef = new WeakReference<ProgressBar>(progressBar);
        }

        protected Bitmap doInBackground(String... urls) {

            return loadImage(urls[0]);
        }

        protected void onPostExecute(Bitmap result) {
            ImageView imgView = imgRef.get();
            if (imgView != null) {
                if (result != null) {
                    imgView.setImageBitmap(result);

                }
            }
            ProgressBar pbView = pbRef.get();
            if (pbView != null) {
                pbView.setVisibility(View.GONE);
            }
        }
    }
}
