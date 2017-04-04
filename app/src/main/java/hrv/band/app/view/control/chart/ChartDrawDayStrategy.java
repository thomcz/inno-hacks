package hrv.band.app.view.control.chart;

import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import hrv.band.app.R;
import hrv.band.app.control.Measurement;
import hrv.band.app.view.adapter.HRVValue;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.SubcolumnValue;

/**
 * Copyright (c) 2017
 * Created by Thomas Czogalik on 07.03.2017
 *
 * Draws parameters into chart which were measured on that day.
 */
public class ChartDrawDayStrategy extends AbstractChartDrawStrategy {

    private static final int COLUMN_COUNT = 24;
    private static final int SUB_COLUMN_COUNT = 4;
    private static final String X_AXIS_LABEL = "Hour";

    @Override
    protected void setChartValues(List<Measurement> parameters, HRVValue hrvValue) {
        Calendar calendar = GregorianCalendar.getInstance();
        for (int i = 0; i < parameters.size(); i++) {
            calendar.setTime(parameters.get(i).getTime());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE) / 15;

            columns[hour].getValues().set(minutes,
                    new SubcolumnValue((float) HRVValue.getHRVValue(hrvValue, parameters.get(i)),
                            ContextCompat.getColor(context, R.color.colorAccent)));
            configColumnLabels(hour);
        }
    }

    @Override
    protected int getColumnCount() {
        return COLUMN_COUNT;
    }

    @Override
    protected int getSubColumnCount() {
        return SUB_COLUMN_COUNT;
    }

    @Override
    protected String getXAxisLabel() {
        return X_AXIS_LABEL;
    }

    @Override
    protected List<AxisValue> getXAxisValues() {
        List<AxisValue> values = new ArrayList<>();
        for (int i = 0; i < COLUMN_COUNT; i++) {
            AxisValue value = new AxisValue(i);
            if (i % 4 == 0) {
                value.setLabel(i + ":00");
                values.add(value);
            }
        }
        return values;
    }

}