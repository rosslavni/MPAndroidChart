
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Path;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;

import java.util.ArrayList;

/**
 * The ScatterChart. Draws dots, triangles, squares and custom shapes into the
 * chartview.
 * 
 * @author Philipp Jahoda
 */
public class ScatterChart extends BarLineChartBase {

    /** enum that defines the shape that is drawn where the values are */
    public enum ScatterShape {
        CROSS, TRIANGLE, CIRCLE, SQUARE, CUSTOM
    }

    public ScatterChart(Context context) {
        super(context);
    }

    public ScatterChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScatterChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Sets a ScatterData object as a model for the ScatterChart.
     * 
     * @param data
     */
    public void setData(ScatterData data) {
        super.setData(data);
    }

    @Override
    protected void drawData() {

        ArrayList<ScatterDataSet> dataSets = (ArrayList<ScatterDataSet>) mCurrentData.getDataSets();

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            ScatterDataSet dataSet = dataSets.get(i);
            ArrayList<Entry> entries = dataSet.getYVals();

            float shapeHalf = dataSet.getScatterShapeSize() / 2f;

            float[] pos = generateTransformedValues(entries, 0f);

            // Get the colors for the DataSet at the current index. If the index
            // is out of bounds, reuse DataSet colors.
            ArrayList<Integer> colors = mCt.getDataSetColors(i % mCt.getColors().size());

            ScatterShape shape = dataSet.getScatterShape();

            for (int j = 0; j < pos.length; j += 2) {

                // Set the color for the currently drawn value. If the index is
                // out of bounds, reuse colors.
                mRenderPaint.setColor(colors.get(j % colors.size()));

                if (isOffContentRight(pos[j]))
                    break;

                // make sure the lines don't do shitty things outside bounds
                if (j != 0 && isOffContentLeft(pos[j - 1])
                        && isOffContentTop(pos[j + 1])
                        && isOffContentBottom(pos[j + 1]))
                    continue;

                if (shape == ScatterShape.SQUARE) {

                    mDrawCanvas.drawRect(pos[j] - shapeHalf, pos[j + 1] - shapeHalf, pos[j]
                            + shapeHalf, pos[j + 1]
                            + shapeHalf, mRenderPaint);

                } else if (shape == ScatterShape.CIRCLE) {

                    mDrawCanvas.drawCircle(pos[j], pos[j + 1], shapeHalf, mRenderPaint);

                } else if (shape == ScatterShape.CROSS) {

                    mDrawCanvas.drawLine(pos[j] - shapeHalf, pos[j + 1], pos[j] + shapeHalf,
                            pos[j + 1], mRenderPaint);
                    mDrawCanvas.drawLine(pos[j], pos[j + 1] - shapeHalf, pos[j], pos[j + 1]
                            + shapeHalf, mRenderPaint);

                } else if (shape == ScatterShape.TRIANGLE) {

                    // create a triangle path
                    Path tri = new Path();
                    tri.moveTo(pos[j], pos[j + 1] - shapeHalf);
                    tri.lineTo(pos[j] + shapeHalf, pos[j + 1] + shapeHalf);
                    tri.lineTo(pos[j] - shapeHalf, pos[j + 1] + shapeHalf);
                    tri.close();

                    mDrawCanvas.drawPath(tri, mRenderPaint);

                } else if (shape == ScatterShape.CUSTOM) {

                    Path customShape = dataSet.getCustomScatterShape();

                    if (customShape == null)
                        return;

                    // transform the provided custom path
                    transformPath(customShape);
                    mDrawCanvas.drawPath(customShape, mRenderPaint);
                }
            }
        }
    }

    @Override
    protected void drawValues() {
        // if values are drawn
        if (mDrawYValues && mCurrentData.getYValCount() < mMaxVisibleCount * mScaleX) {

            ArrayList<ScatterDataSet> dataSets = (ArrayList<ScatterDataSet>) mCurrentData
                    .getDataSets();

            for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

                ScatterDataSet dataSet = dataSets.get(i);
                ArrayList<Entry> entries = dataSet.getYVals();

                float[] positions = generateTransformedValues(entries, 0f);

                float shapeSize = dataSet.getScatterShapeSize();

                for (int j = 0; j < positions.length; j += 2) {

                    if (isOffContentRight(positions[j]))
                        break;

                    if (isOffContentLeft(positions[j]) || isOffContentTop(positions[j + 1])
                            || isOffContentBottom(positions[j + 1]))
                        continue;

                    float val = entries.get(j / 2).getVal();

                    if (mDrawUnitInChart) {

                        mDrawCanvas.drawText(mFormatValue.format(val) + mUnit, positions[j],
                                positions[j + 1] - shapeSize, mValuePaint);
                    } else {

                        mDrawCanvas.drawText(mFormatValue.format(val), positions[j],
                                positions[j + 1] - shapeSize,
                                mValuePaint);
                    }
                }
            }
        }
    }

    @Override
    protected void drawHighlights() {

        // if there are values to highlight and highlighnting is enabled, do it
        if (mHighlightEnabled && mHighLightIndicatorEnabled && valuesToHighlight()) {

            for (int i = 0; i < mIndicesToHightlight.length; i++) {

                DataSet set = getDataSetByIndex(mIndicesToHightlight[i].getDataSetIndex());

                int xIndex = mIndicesToHightlight[i].getXIndex(); // get the
                                                                  // x-position
                float y = set.getYValForXIndex(xIndex); // get the y-position

                float[] pts = new float[] {
                        xIndex, mYChartMax, xIndex, mYChartMin, 0, y, mDeltaX, y
                };

                transformPointArray(pts);
                // draw the highlight lines
                mDrawCanvas.drawLines(pts, mHighlightPaint);
            }
        }
    }

    @Override
    protected void drawAdditional() {

    }

    /**
     * Returns all possible predefined scattershapes.
     * 
     * @return
     */
    public static ScatterShape[] getAllPossibleShapes() {
        return new ScatterShape[] {
                ScatterShape.SQUARE, ScatterShape.CIRCLE, ScatterShape.TRIANGLE, ScatterShape.CROSS
        };
    }
}
