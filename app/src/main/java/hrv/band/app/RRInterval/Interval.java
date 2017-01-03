package hrv.band.app.RRInterval;

import java.util.Date;

/**
 * Created by Julian on 11.06.2016.
 */
public class Interval {

    //RR Interval Data in ms
    private double[] rrInterval;
    private Date startTime;

    public Interval(Date startTime, double[] rrInterval) {
        this.startTime = startTime;
        this.rrInterval = rrInterval;
    }

    public Interval(Date startTime) {
        this.startTime = startTime;
    }

    public double[] GetRRInterval()
    {
        return rrInterval;
    }

    public void SetRRInterval(Double[] rrInterval)
    {
        this.rrInterval = new double[rrInterval.length];
        for (int i= 0; i < rrInterval.length;i++ ) {
            this.rrInterval[i] = rrInterval[i];
        }
    }

    public Date GetStartTime()
    {
        return startTime;
    }

    public void SetStartTime(Date time)
    {
        startTime = time;
    }

    public String printRR () {
        String s = null;
        for (double d: rrInterval) {
            s= s+","+d;
        }
        return s;
    }
}
