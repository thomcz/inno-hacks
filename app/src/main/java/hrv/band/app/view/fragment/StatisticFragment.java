package hrv.band.app.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import hrv.band.app.Control.HRVParameters;
import hrv.band.app.R;
import hrv.band.app.view.HRVValueActivity;
import hrv.band.app.view.MainActivity;
import hrv.band.app.view.StatisticActivity;
import hrv.band.app.view.adapter.HRVValue;
import hrv.band.app.view.adapter.StatisticValueAdapter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

import static hrv.band.app.view.StatisticActivity.RESULT_DELETED;

/**
 * Created by Thomas on 27.06.2016.
 */
public class StatisticFragment extends Fragment {
    private static final String ARG_SECTION_VALUE = "sectionValue";
    private static final String ARG_HRV_VALUE = "hrvValue";
    private static final String ARG_DATE_VALUE = "dateValue";
    private StatisticValueAdapter adapter;
    //private final String dateFormat = "dd.MMM yyyy";
    private View rootView;
    //protected BarChart mChart;
    private ColumnChartView mChart;
    private Column[] columns;
    private List<int[]> chartValuesIndex;
    private HRVValue hrvType;
    private List<HRVParameters> parameters;
    private TextView date;

    public StatisticFragment() {
    }

    public static StatisticFragment newInstance(HRVValue type, ArrayList<HRVParameters> parameters,
                                                Date date) {
        StatisticFragment fragment = new StatisticFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SECTION_VALUE, type);
        args.putParcelableArrayList(ARG_HRV_VALUE, parameters);
        args.putSerializable(ARG_DATE_VALUE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_statistic_fragment, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.stats_measure_history);

        parameters = getArguments().getParcelableArrayList(ARG_HRV_VALUE);
        hrvType = (HRVValue) getArguments().getSerializable(ARG_SECTION_VALUE);

        date = (TextView) rootView.findViewById(R.id.stats_date);
        TextView desc = (TextView) rootView.findViewById(R.id.stats_value_desc);
        TextView type = (TextView) rootView.findViewById(R.id.stats_type);

        date.setText(formatDate((Date) getArguments().getSerializable(ARG_DATE_VALUE)));

        desc.setText(hrvType.toString());
        type.setText(hrvType.getUnit());


        adapter = new StatisticValueAdapter(getActivity().getApplicationContext(),
                hrvType, parameters);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Intent intent = new Intent(getContext(), HRVValueActivity.class);
                intent.putExtra(MainActivity.HRV_PARAMETER_ID, parameters.get(position));
                intent.putExtra(MainActivity.HRV_DATE, date.getText());
                startActivityForResult(intent, 1);
            }

        });

        mChart = (ColumnChartView) rootView.findViewById(R.id.stats_chart);
        initChart(parameters);


        return rootView;
    }

    private String formatDate(Date date) {
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(getContext());
        return dateFormat.format(date);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_DELETED){
            Activity root = getActivity();
            if (root instanceof StatisticActivity) {
                Date date = (Date) getArguments().getSerializable(ARG_DATE_VALUE);
                ((StatisticActivity) root).updateFragments(date);
            }
        }
    }


    private void initChart(List<HRVParameters> parameters) {
        int numSubcolumns = 4;
        int numColumns = 24;

        chartValuesIndex = new ArrayList<>();
        columns = new Column[numColumns];

        for (int i = 0; i < numColumns; i++) {
            ArrayList<SubcolumnValue> subColumns = new ArrayList<>();
            for (int j = 0; j < numSubcolumns; j++) {
                subColumns.add(new SubcolumnValue());
            }
            columns[i] = new Column(subColumns);
        }
        setChartValues(parameters);
        setAxis();
    }

    private void setAxis() {
        ColumnChartData data = new ColumnChartData(new ArrayList<>(Arrays.asList(columns)));

        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("Hour");
        axisY.setName(hrvType.getUnit());
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        mChart.setZoomEnabled(false);
        mChart.setColumnChartData(data);
    }

    private void setChartValues(List<HRVParameters> parameters) {
        resetChartValues();
        for (int i = 0; i < parameters.size(); i++) {
            Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
            calendar.setTime(parameters.get(i).getTime());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE) / 15;

            columns[hour].getValues().set(minutes,
                    new SubcolumnValue((float) HRVValue.getHRVValue(hrvType, parameters.get(i)),
                            ContextCompat.getColor(getContext(), R.color.colorAccent)));
            chartValuesIndex.add(new int[] {hour, minutes});
            columns[hour].setHasLabels(false);
            columns[hour].setHasLabelsOnlyForSelected(false);
        }
        setAxis();
    }

    private void resetChartValues() {
        for (int i = 0; i < chartValuesIndex.size(); i++) {
            int[] tuple = chartValuesIndex.get(i);
            columns[tuple[0]].getValues().set(tuple[1], new SubcolumnValue());
        }
        chartValuesIndex = new ArrayList<>();
    }

    private void setDate() {
        if (rootView == null) {
            return;
        }
        //TextView date = (TextView) rootView.findViewById(R.id.stats_date);
        date.setText(formatDate((Date) getArguments().getSerializable(ARG_DATE_VALUE)));
    }

    public void updateValues(ArrayList<HRVParameters> parameters, Date date) {
        this.parameters = parameters;
        getArguments().putParcelableArrayList(ARG_HRV_VALUE, parameters);
        getArguments().putSerializable(ARG_DATE_VALUE, date);
        setDate();
        if (adapter != null) {
            adapter.setDataset(parameters);
            setChartValues(parameters);
        }
    }
}
