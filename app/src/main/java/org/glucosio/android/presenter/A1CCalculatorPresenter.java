/*
 * Copyright (C) 2016 Glucosio Foundation
 *
 * This file is part of Glucosio.
 *
 * Glucosio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Glucosio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Glucosio.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.glucosio.android.presenter;

import org.glucosio.android.activity.A1cCalculator;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.HB1ACReading;
import org.glucosio.android.db.User;
import org.glucosio.android.tools.GlucosioConverter;

import java.util.Calendar;

public class A1CCalculatorPresenter {
    private DatabaseHandler dB;
    private A1cCalculator activity;


    public A1CCalculatorPresenter(A1cCalculator a1CCalculator) {
        this.activity = a1CCalculator;
        dB = new DatabaseHandler(a1CCalculator.getApplicationContext());
    }

    public double calculateA1C(String glucose) {
        GlucosioConverter converter = new GlucosioConverter();
        double convertedA1C;
        User user = dB.getUser(1);

        if ("mg/dL".equals(user.getPreferred_unit())) {
            convertedA1C = converter.glucoseToA1C(Double.parseDouble(glucose));
        } else {
            convertedA1C = converter.glucoseToA1C(converter.glucoseToMgDl(Double.parseDouble(glucose)));
        }
        if (!"percentage".equals(user.getPreferred_unit_a1c())) {
            return converter.a1cNgspToIfcc(convertedA1C);
        } else {
            return convertedA1C;
        }
    }

    public void checkGlucoseUnit() {
        if (!dB.getUser(1).getPreferred_unit().equals("mg/dL")) {
            activity.setMmol();
        }
    }

    public void saveA1C(double a1c) {
        HB1ACReading a1cReading = new HB1ACReading(a1c, Calendar.getInstance().getTime());
        dB.addHB1ACReading(a1cReading);
        activity.finish();
    }

    public String getA1cUnit() {
        return dB.getUser(1).getPreferred_unit_a1c();
    }

}
