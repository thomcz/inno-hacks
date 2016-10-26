package hrv.band.aurora.storage.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hrv.band.aurora.Control.HRVParameters;
import hrv.band.aurora.common.FileUtils;
import hrv.band.aurora.storage.IStorage;
import hrv.band.aurora.view.adapter.CategorySpinnerAdapter;

/**
 * Created by Julian on 23.06.2016.
 */
public class SQLController implements IStorage {

    @Override
    public void saveData(Context context, List<HRVParameters> parameters) {

        for (HRVParameters param : parameters) {
            saveData(context, param);
        }
    }

    @Override
    public void saveData(Context context, HRVParameters parameter) {
        SQLiteStorageController controller = new SQLiteStorageController(context);

        SQLiteDatabase db = controller.getWritableDatabase();

        ContentValues valuesParams = new ContentValues();
        long time = parameter.getTime().getTime();

        valuesParams.put(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_TIME, time);
        valuesParams.put(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_SD1, parameter.getSd1());
        valuesParams.put(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_SD2, parameter.getSd2());
        valuesParams.put(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_LF, parameter.getLf());
        valuesParams.put(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_HF, parameter.getHf());
        valuesParams.put(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_RMSSD, parameter.getRmssd());
        valuesParams.put(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_SDNN, parameter.getSdnn());
        valuesParams.put(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_BAEVSKY, parameter.getBaevsky());
        valuesParams.put(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_RATING, parameter.getRating());
        valuesParams.put(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_CATEGORY, parameter.getCategory().getText(context.getResources()));
        valuesParams.put(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_NOTE, parameter.getNote());

        long firstId = db.insert(HRVParameterContract.HRVParameterEntry.TABLE_NAME,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_HF,
                valuesParams);

        db.close();

        SQLiteDatabase db2 = controller.getWritableDatabase();
        db2.beginTransaction();

        for (Double rrVal : parameter.getRRIntervals()) {
            ContentValues valuesRR = new ContentValues();
            valuesRR.put(RRIntervalContract.RRIntercalEntry.COLUMN_NAME_ENTRY_ID, firstId);
            valuesRR.put(RRIntervalContract.RRIntercalEntry.COLUMN_NAME_ENTRY_VALUE, rrVal);

            db2.insert(RRIntervalContract.RRIntercalEntry.TABLE_NAME,
                    RRIntervalContract.RRIntercalEntry.COLUMN_NAME_ENTRY_VALUE,
                    valuesRR);
        }
        db2.setTransactionSuccessful();
        db2.endTransaction();
        db2.close();
    }

    @Override
    public List<HRVParameters> loadData(Context context, Date date) {

        long millisecondsInOneDay = 1000 * 60 * 60 * 24;
        long dateTimeInMilis = date.getTime();
        long startSearchTime = dateTimeInMilis - millisecondsInOneDay;

        List<HRVParameters> returnList = new ArrayList<HRVParameters>();
        SQLiteStorageController controller = new SQLiteStorageController(context);

        SQLiteDatabase db = controller.getReadableDatabase();

        String[] projection = {
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_ENTRY_ID,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_TIME,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_SD1,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_SD2,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_LF,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_HF,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_RMSSD,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_SDNN,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_BAEVSKY,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_RATING,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_CATEGORY,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_NOTE
        };

        String timeStr = Long.toString(date.getTime());

        Cursor c = db.query(
                HRVParameterContract.HRVParameterEntry.TABLE_NAME,
                projection,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_TIME + " BETWEEN ? AND ?",
                new String[]{Long.toString(startSearchTime), timeStr},
                null,
                null,
                null,
                null
        );

        if (c.getCount() == 0)
            return returnList;

        c.moveToFirst();

        do {

            HRVParameters newParam = new HRVParameters();
            int rrid = c.getInt(c.getColumnIndex(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_ENTRY_ID));
            long time = c.getLong(c.getColumnIndex(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_TIME));
            Date timeAsDate = new Date(time);

            newParam.setTime(timeAsDate);
            newParam.setSd1(c.getFloat(c.getColumnIndex(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_SD1)));
            newParam.setSd2(c.getFloat(c.getColumnIndex(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_SD2)));
            newParam.setLf(c.getFloat(c.getColumnIndex(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_LF)));
            newParam.setHf(c.getFloat(c.getColumnIndex(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_HF)));
            newParam.setRmssd(c.getFloat(c.getColumnIndex(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_RMSSD)));
            newParam.setSdnn(c.getFloat(c.getColumnIndex(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_SDNN)));
            newParam.setBaevsky(c.getFloat(c.getColumnIndex(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_BAEVSKY)));
            newParam.setRating(c.getFloat(c.getColumnIndex(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_RATING)));
            String category = c.getString(c.getColumnIndex(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_CATEGORY));
            newParam.setCategory(CategorySpinnerAdapter.MeasureCategory.valueOf(category.toUpperCase()));
            newParam.setNote(c.getString(c.getColumnIndex(HRVParameterContract.HRVParameterEntry.COLUMN_NAME_NOTE)));

            //Laden der rr daten
            Cursor crr = db.query(
                    RRIntervalContract.RRIntercalEntry.TABLE_NAME,
                    null,  //All Columns
                    RRIntervalContract.RRIntercalEntry.COLUMN_NAME_ENTRY_ID + " = ?",
                    new String[]{Integer.toString(rrid)},
                    null,
                    null,
                    null,
                    null
            );

            if (crr.getCount() == 0)
                return returnList;

            ArrayList<Double> rrValues = new ArrayList<Double>();
            crr.moveToFirst();
            if (!crr.isAfterLast()) {
                do {
                    int columnIndex = crr.getColumnIndex(RRIntervalContract.RRIntercalEntry.COLUMN_NAME_ENTRY_VALUE);
                    double loadedValue = crr.getDouble(columnIndex);
                    rrValues.add(loadedValue);
                } while (crr.moveToNext());
            }

            newParam.setRRIntervals(rrValues);
            returnList.add(newParam);
        } while (c.moveToNext());

        return returnList;
    }

    @Override
    public boolean deleteData(Context context, HRVParameters parameter) {
        SQLiteStorageController controller = new SQLiteStorageController(context);

        SQLiteDatabase db = controller.getReadableDatabase();


        String[] projection = {
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_ENTRY_ID,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_TIME,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_SD1,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_SD2,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_LF,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_HF,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_RMSSD,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_SDNN,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_BAEVSKY,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_RATING,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_CATEGORY,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_NOTE
        };

        String timeStr = Long.toString(parameter.getTime().getTime());

        return db.delete(HRVParameterContract.HRVParameterEntry.TABLE_NAME,
                HRVParameterContract.HRVParameterEntry.COLUMN_NAME_TIME + " EQUALS ? ",
                new String[]{timeStr}
        ) > 0;
    }

    @Override
    public boolean deleteData(Context context, List<HRVParameters> parameters) {
        for (int i = 0; i < parameters.size(); i++) {
            deleteData(context, parameters.get(i));
        }

        return true;
    }

    public boolean exportDB(String dbPath, Context con) throws IOException {
        SQLiteStorageController controller = new SQLiteStorageController(con);
        String dbName = controller.getDatabaseName();

        File exportDestinationFile = new File(dbPath);
        File dbToExport = new File(dbName);

        if(dbToExport.exists())
        {
            FileUtils.copyFile(new FileInputStream(dbToExport), new FileOutputStream(exportDestinationFile));

            return true;
        }

        return false;
    }

    public boolean importDB(String dbPath, Context con) throws IOException {

        SQLiteStorageController controller = new SQLiteStorageController(con);
        String dbName = controller.getDatabaseName();

        File newDB = new File(dbPath);
        File oldDB = new File(dbName);

        if(newDB.exists()) {
            FileUtils.copyFile(new FileInputStream(newDB), new FileOutputStream(oldDB));
            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
            controller.getWritableDatabase().close();

            return true;
        }

        return false;
    }
}

