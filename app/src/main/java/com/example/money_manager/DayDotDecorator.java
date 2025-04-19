package com.example.money_manager;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class DayDotDecorator implements DayViewDecorator {

    private final CalendarDay day;
    private final int[] colors;
    private final float radius;

    public DayDotDecorator(CalendarDay day, int[] colors, float radius) {
        this.day = day;
        this.colors = colors;
        this.radius = radius;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return this.day.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new MultiDotSpan(radius, colors));
    }
}
