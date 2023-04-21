package adhi.avi.animaldetection;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class listitem extends BaseAdapter {
    ArrayList<String> name,date;
    private Context context;
    public listitem(Context appcontext,  ArrayList name, ArrayList date) {

        this.context=appcontext;


        this.name=name;
        this.date=date;


    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i,View view,ViewGroup viewGroup) {
        LayoutInflater inflator=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        if(view==null)
        {
            gridView=new View(context);
            //gridView=inflator.inflate(R.layout.customview, null);
            gridView=inflator.inflate(R.layout.listitem,null);

        }
        else
        {
            gridView=(View)view;

        }
        TextView tvname=(TextView)gridView.findViewById(R.id.tv_date);
        TextView tvcomment=(TextView)gridView.findViewById(R.id.tv_name);

        tvname.setTextColor(Color.BLACK);
        tvcomment.setTextColor(Color.BLACK);



        tvname.setText(name.get(i));
        tvcomment.setText(date.get(i));


        return gridView;
    }
}
