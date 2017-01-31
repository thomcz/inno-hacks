package adapter;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.BeforeClass;

import java.text.DecimalFormat;
import java.util.Date;

import hrv.band.app.Control.HRVParameters;
import hrv.band.app.R;
import hrv.band.app.view.adapter.HRVValue;
import hrv.band.app.view.fragment.MeasureValueFragment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Copyright (c) 2017
 * Created by Thomas Czogalik on 24.01.2017
 */


public class ValueAdapterTest extends AbstractAdapterTest {

    private static HRVParameters parameter;

    @BeforeClass
    public static void setUpData() throws Exception {
        parameter = new HRVParameters(new Date(1000), 0, 0, 0, 0, 0, 0, 0, 0, new double[] {1,1,1,1,1,1});
    }

    @Override
    public Fragment getFragment() {
        return MeasureValueFragment.newInstance(parameter);
    }

    @Override
    public ListView getListView() {
        return (ListView) fragment.getActivity().findViewById(R.id.hrv_value_list);
    }

    @Override
    public int getSize() {
        return HRVValue.values().length;
    }

    @Override
    public void checkViewElement(View view, int position) {
        TextView descText = (TextView) view.findViewById(R.id.measure_value_desc);
        TextView valueText = (TextView) view.findViewById(R.id.hrv_value);
        TextView unitText = (TextView) view.findViewById(R.id.measure_value_unit);

        assertNotNull(descText);
        assertNotNull(valueText);
        assertNotNull(unitText);

        assertEquals(HRVValue.values()[position].toString(), descText.getText());

        double value = HRVValue.getHRVValue(HRVValue.values()[position], parameter);
        assertEquals(new DecimalFormat("#.##").format(value), valueText.getText());

        assertEquals(HRVValue.values()[position].getUnit(), unitText.getText());

    }

    @Override
    public Object getItemAtIndex(int index) {
        return parameter;
    }

    @Override
    public View getItemLayout() {
        return fragment.getActivity().findViewById(R.id.hrv_value_list);
    }
}
