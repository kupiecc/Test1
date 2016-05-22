package net.jackapp.auctionchecker;

import android.content.Context;

import java.io.File;

/**
 * Created by jacekkupczak on 18.05.16.
 */
public class FileCache {
    private File cacheDir;

    public FileCache(Context context) {

//        if((Environment.getExternalStorageState()).equals(Environment.MEDIA_MOUNTED)) {
//            cacheDir = new File(Environment.getExternalStorageDirectory(), "auctionCheckerImages");
//        } else {
//            cacheDir = context.getCacheDir();
//        }
//
            cacheDir = context.getCacheDir();
//        cacheDir = new File(context.getFilesDir(), "auctionCheckerImages");
//

        if (!cacheDir.mkdirs()) {
            cacheDir.mkdirs();
        }
    }

    public File getFile(String url) {
        String filename = String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;
    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null) {
            System.out.println("FileCache clear fileslist null");
            return;
        }
        System.out.println("FileCache clear files.length = " + files.length);
        for (File f : files) {
            f.delete();
            System.out.println("FileCache clear delete = " + f.getName());
        }
    }
}
