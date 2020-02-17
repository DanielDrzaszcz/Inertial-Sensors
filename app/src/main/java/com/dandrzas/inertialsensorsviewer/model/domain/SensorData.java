package com.dandrzas.inertialsensorsviewer.model.domain;

public class SensorData
{
    private float[] value = new float[3];
    private float minDelay;

    public SensorData()
    {
    }

    public void setValue(float[] value)
    {
        this.value = value;
    }

    public float[] getValue()
    {
        return value;
    }

    public void setMinDelay(float value)
    {
        this.minDelay = value;
    }

    public float getMinDelay()
    {
        return minDelay;
    }
}