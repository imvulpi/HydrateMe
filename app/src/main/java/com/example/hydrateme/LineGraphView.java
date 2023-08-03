package com.example.hydrateme;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LineGraphView extends View {
    private List<Float> dataPoints;

    public LineGraphView(Context context) {
        super(context);
        init();
    }

    public LineGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dataPoints = new ArrayList<>();
    }

    public void setData(List<Float> data) {
        dataPoints = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (dataPoints.isEmpty()) {
            return; // no data to draw
        }
        int dataSize = dataPoints.size();

        float width = canvas.getWidth();
        float height = canvas.getHeight();
        float biggestValue = 0;
        float heightPadding = height * 0.1f; // Adjust the padding value as a fraction of the canvas height
        float widthPadding = width * 0.05f;
        float adjustedHeight = (height - 3);
        float adjustedWidth = width;

        canvas.translate(width, height); // Translate the origin to the bottom-left corner
        canvas.scale(-1, -1); // Scale the canvas vertically by -1
        canvas.translate(-60,0);

        for (int i = 0; i < dataPoints.size(); i++) {
            if(dataPoints.get(i) > biggestValue){
                biggestValue = dataPoints.get(i);
            }
        }

        Log.d("Value Checker","Biggest value "+biggestValue);
        Log.d("Value Checker","width = "+width+" height = "+height);


        Paint linePaint = new Paint();
        linePaint.setColor(Color.rgb(0, 114, 198));
        linePaint.setStrokeWidth(2.5f);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);

        Path path = new Path();
        float startX = width / dataSize;

        for (int i = 0; i < dataSize; i++) {
            float currentValue = (dataPoints.get(i) * adjustedHeight / biggestValue);
            float x = (startX + (i * adjustedWidth / dataSize));

            if (i == 0) {
                path.moveTo(x, currentValue);
            }

            path.lineTo(x, currentValue);
        }

        canvas.drawPath(path, linePaint);

        Path circlePath = new Path();
        linePaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < dataSize; i++) {
            float offset = 0;
            float currentValue = (dataPoints.get(i) * adjustedHeight / biggestValue);
            float x = (startX + (i * adjustedWidth / dataSize));
    
            Log.d("Value Checker","Value of Data: "+dataPoints.get(i)+" Offset: "+offset);
            if(dataPoints.get(i) == 0){
                offset += 10;
            }else if(dataPoints.get(i) == biggestValue){
                offset -= 8;
            }


            circlePath.addCircle(x, currentValue + offset, 10, Path.Direction.CW);
        }

        canvas.drawPath(circlePath, linePaint);
    }
}

