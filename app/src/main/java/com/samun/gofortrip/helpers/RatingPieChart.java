package com.samun.gofortrip.helpers;

import android.graphics.Color;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;

import java.util.List;

public class RatingPieChart {

    public static void create(List<PieEntry> pieEntries, List<Integer> colors, PieChart pieChart, String centerTxt) {
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16);
        pieDataSet.setValueFormatter(new DefaultValueFormatter(0));

        PieData pieData = new PieData(pieDataSet);

        pieChart.clear();
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);

        if (pieEntries.size() == 0) {

            pieChart.setCenterText("No Review Available");
            pieChart.setBackgroundColor(Color.parseColor("#EBEBEB"));
            pieChart.setCenterTextSize(16);

        } else {

            pieChart.setCenterTextSize(20);
            pieChart.setCenterText(centerTxt);

        }

        pieChart.animate();


    }


}
