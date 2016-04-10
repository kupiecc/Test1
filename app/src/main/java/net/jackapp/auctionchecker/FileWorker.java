package net.jackapp.auctionchecker;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jacekkupczak on 08.04.16.
 */
public class FileWorker {

    public void createJsonFile(Context context, String jsonFileName) throws JSONException {

        File jsonFile = new File(context.getApplicationContext().getFilesDir(), jsonFileName);

        try{
            if(!jsonFile.exists()){

                jsonFile.createNewFile();
                Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_LONG).show();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeJsonFile(Context context, String jsonToSaveString, String jsonFileName) {

        File jsonFile = new File(context.getApplicationContext().getFilesDir(), jsonFileName);
//        String jsonToSaveString = jsonToSave;
        FileOutputStream os;
        try{
            if(!jsonFile.exists())
            {
                jsonFile.createNewFile();
            }
            os = context.openFileOutput(jsonFileName, Context.MODE_PRIVATE);
            os.write(jsonToSaveString.getBytes());
            os.close();
//            Toast.makeText(context.getApplicationContext(), "Auction saved", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFile(Context context, String fileName) throws JSONException {

        InputStream in = null;
        String readString = "";
        File jsonFile = new File(context.getApplicationContext().getFilesDir(), fileName);

        try{
            createJsonFile(context, fileName);
            in = new BufferedInputStream(new FileInputStream(jsonFile));
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            readString = new String(buffer, "UTF-8");

        } catch (IOException e) {
            Log.d("jk", e.getMessage());
            e.printStackTrace();
        }

        return readString;

    }
    public String readFile(Context context, String fileName, AssetManager assetManager) throws JSONException {

        InputStream in = null;
        StringBuilder readString = new StringBuilder();
        BufferedReader reader = null;

        try{
            reader = new BufferedReader(new InputStreamReader(assetManager.open(fileName), "UTF-8"));
            String line = "";
            while ((line = reader.readLine()) != null){
                readString.append(line);
            }
        } catch (IOException e) {
            Log.d("jk", e.getMessage());
            e.printStackTrace();
        } finally {
            try{
                if(reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return readString.toString();

    }

}
