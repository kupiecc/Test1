package net.jackapp.auctionchecker;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jacekkupczak on 08.04.16.
 */
public class FileWorker {

    public void createJsonFile(Context context, String jsonFileName) throws JSONException {

        File jsonFile = new File(context.getApplicationContext().getFilesDir(), jsonFileName);

        try{
            if(!jsonFile.exists()){

                jsonFile.createNewFile();
                Toast.makeText(context.getApplicationContext(), "File created", Toast.LENGTH_LONG).show();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeJsonFile(Context context, JSONObject jsonToSave, String jsonFileName) {

        File jsonFile = new File(context.getApplicationContext().getFilesDir(), jsonFileName);
        String jsonToSaveString = jsonToSave.toString();
        FileOutputStream os;
        try{
            if(!jsonFile.exists())
            {
                jsonFile.createNewFile();
            }
            os = context.openFileOutput(jsonFileName, Context.MODE_PRIVATE);
            os.write(jsonToSaveString.getBytes());
            os.close();
            Toast.makeText(context.getApplicationContext(), "Json saved", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readJsonFile(Context context, String jsonFileName) throws JSONException {

        InputStream in = null;
        String jsonDBString = "{Json: File Empty}";
        File jsonFile = new File(context.getApplicationContext().getFilesDir(), jsonFileName);

        try{

            in = new BufferedInputStream(new FileInputStream(jsonFile));
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            jsonDBString = new String(buffer, "UTF-8");
            Log.d("jk my value", jsonDBString);

        } catch (IOException e) {
            Log.d("jk", e.getMessage());
            e.printStackTrace();
        }

        return jsonDBString;

    }

}
