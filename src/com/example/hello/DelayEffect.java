package com.example.hello;

import android.util.Log;

/**
 * Created by michael on 4/1/14.
 */
public class DelayEffect {

    private DelayLine delayBuf;
    private double wetGain;
    private double dryGain;
    private double feedbackGain;

    public DelayEffect(int inDelay)
    {
        delayBuf = new DelayLine(inDelay);
    }

    public void setDelayTime(double newDelay)
    {
        delayBuf.setDelayLineDelay(newDelay);
    }

    public double getDelay()
    {
        return delayBuf.getDelayLineDelay();
    }

    public void setWetGain(double newGain)
    {
        wetGain = newGain;
    }

    public double getWetGain()
    {
        return wetGain;
    }

    public void setDryGain(double newGain)
    {
        dryGain = newGain;
    }

    public double getDryGain()
    {
        return dryGain;
    }

    public void setFeedbackGain(double newGain)
    {
        feedbackGain = newGain;
    }

    public double getFeedbackGain()
    {
        return feedbackGain;
    }


    double tick(double input)
    {
        double output;
        double temp = feedbackGain * delayBuf.getCurrentOut();
        output = delayBuf.tick(input + temp);
        //Log.d("Delay", "output = " + output);
        output = (input * dryGain) + (output * wetGain);
        //adjust for clipping
        if (output > 1.0)
            output = 1.0;
        else if (output < -1.0)
            output = -1.0;

        return output;
//        return input * dryGain;
    }
}
