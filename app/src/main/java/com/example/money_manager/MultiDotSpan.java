package com.example.money_manager;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

public class MultiDotSpan implements LineBackgroundSpan {

    private final float radius;
    private final int[] colors;

    /**
     * @param radius The radius of each dot.
     * @param colors An array of colors for the dots.
     */
    public MultiDotSpan(float radius, int[] colors) {
        this.radius = radius;
        this.colors = colors;
    }

    @Override
    public void drawBackground(Canvas canvas, Paint paint,
                               int left, int right, int top, int baseline, int bottom,
                               CharSequence text, int start, int end, int lineNum) {
        int count = colors.length;
        if(count == 0) return;

        // Total width occupied by dots (each dot has diameter 2*radius plus spacing)
        float totalWidth = count * (2 * radius) + (count - 1) * radius;
        // Center horizontally in the cell
        float centerX = (left + right) / 2f;
        float startX = centerX - totalWidth / 2f + radius;
        // Position the dots near the bottom of the cell.
        float y = bottom - radius;
        for (int i = 0; i < count; i++) {
            paint.setColor(colors[i]);
            float x = startX + i * (2 * radius + radius);
            canvas.drawCircle(x, y, radius, paint);
        }
    }
}
