package common.pay.sdk;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * TODO 类描述
 *
 * @author HB.fee
 * @date 2022-03-06
 */
public class MyFactory implements LayoutInflater.Factory {
    LayoutInflater.Factory factory = null;

    public void getLayoutInflaterFactor(Activity context){
        LayoutInflater layoutInflater = context.getLayoutInflater();
        if (layoutInflater != null) {
             factory = layoutInflater.getFactory();
        }
    }
    /**
     * Hook you can supply that is called when inflating from a LayoutInflater.
     * You can use this to customize the tag names available in your XML
     * layout files.
     *
     * <p>
     * Note that it is good practice to prefix these custom names with your
     * package (i.e., com.coolcompany.apps) to avoid conflicts with system
     * names.
     *
     * @param name    Tag name to be inflated.
     * @param context The context the view is being created in.
     * @param attrs   Inflation attributes as specified in XML file.
     * @return View Newly created view. Return null for the default
     * behavior.
     */
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View v = factory.onCreateView(name, context, attrs);
        if (v instanceof TextView) {
            
        }

        return null;
    }
}
