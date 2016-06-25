package hrv.band.aurora.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hrv.band.aurora.R;

/**
 * Created by Thomas on 20.06.2016.
 */
public class OverviewValueAdapter extends AbstractValueAdapter {

    private Context context;
    private int layout;

    public OverviewValueAdapter(Context context, int textViewResourceId) {
        super(context);
        this.layout = textViewResourceId;
        this.context = context;
    }

    @Override
    public Object getItem(int i) {
        return getValues()[i];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(layout, parent, false);
        TextView firstLine = (TextView) rowView.findViewById(R.id.firstLine);

        setTextView(firstLine, getValues()[position]);
        return rowView;
    }
}
