package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by Philipp Jahoda on 28/01/16.
 * <p/>
 * A formatter specifically for stacked BarChart that allows to specify whether the all stack values
 * or just the top value should be drawn.
 */
public class StackedValueFormatter implements ValueFormatter {

    /**
     * if true, all stack values of the stacked bar entry are drawn, else only top
     */
    private boolean mDrawWholeStack;
    private FormattedStringCache mFormattedStringCacheWholeStack;
    private FormattedStringCache mFormattedStringCache;


    /**
     * a string that should be appended behind the value
     */
    private String mAppendix;

    private DecimalFormat mFormat;

    /**
     * Constructor.
     *
     * @param drawWholeStack if true, all stack values of the stacked bar entry are drawn, else only top
     * @param appendix       a string that should be appended behind the value
     * @param decimals       the number of decimal digits to use
     */
    public StackedValueFormatter(boolean drawWholeStack, String appendix, int decimals) {
        this.mDrawWholeStack = drawWholeStack;
        this.mAppendix = appendix;

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < decimals; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        this.mFormattedStringCache = new FormattedStringCache(new DecimalFormat("###,###,###,##0" + b.toString()));
        this.mFormattedStringCacheWholeStack = new FormattedStringCache(new DecimalFormat("###,###,###,##0" + b.toString()));
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

        FormattedStringCache chosenCache = mFormattedStringCache;
        int chosenIndex = dataSetIndex;
        float chosenValue = value;
        if (!mDrawWholeStack && entry instanceof BarEntry) {

            BarEntry barEntry = (BarEntry) entry;
            float[] vals = barEntry.getYVals();

            if (vals != null) {

                // find out if we are on top of the stack
                if (vals[vals.length - 1] == value) {
                    chosenCache = mFormattedStringCacheWholeStack;
                    chosenValue = barEntry.getY();
                } else {
                    chosenCache = null;
                }
            }
        }

        if(chosenCache == null){
            return "";
        }

        // return the "proposed" value
        return chosenCache.getFormattedString(chosenValue, chosenIndex) + mAppendix;
    }
}
