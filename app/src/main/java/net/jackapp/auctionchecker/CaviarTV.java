package net.jackapp.auctionchecker;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by jacekkupczak on 09.04.16.
 */
public class CaviarTV extends TextView {

    public CaviarTV(Context context, AttributeSet attrs){
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "cd.ttf"));
    }

}
