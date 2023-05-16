/*
 * Range.java
 *
 * Created on December 02, 2005, 12:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.tools.formatter;

import java.io.Serializable;

public class Range implements Serializable {
    Comparable minValue;
    Comparable maxValue;

    public Range(Comparable minValue, Comparable maxValue) {
        setMinValue(minValue);
        setMaxValue(maxValue);
    }

    public Comparable getMinValue() { return minValue; }
    public void setMinValue(Comparable minValue) {
        this.minValue = minValue;
    }

    public Comparable getMaxValue() { return maxValue; }
    public void setMaxValue(Comparable maxValue) {
        this.maxValue = maxValue;
    }

    boolean isInRange(Comparable value) {
        if (value == null)
            return false;
        if ((minValue == null || value.compareTo(minValue) >= 0) &&
            (maxValue == null || value.compareTo(maxValue) <= 0))
                return true;
        return false;
    }
}
