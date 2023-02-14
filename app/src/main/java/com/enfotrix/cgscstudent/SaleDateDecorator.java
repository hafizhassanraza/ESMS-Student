package com.enfotrix.cgscstudent;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class SaleDateDecorator implements DayViewDecorator {
    private Context context;
    private CalendarDay day;
    private String status;


    public SaleDateDecorator(Context context, CalendarDay day, String status) {
        this.day = day;
        this.context = context;
        this.status = status;

    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return this.day.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {

        if (status.equalsIgnoreCase("Present")) {
            view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_present_circle));
        } else if (status.equalsIgnoreCase("Absent")){
            view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_absent_circle));
        } else if (status.equalsIgnoreCase("Leave")){
            view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_leave_circle));
        }

    }
}
