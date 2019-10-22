/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.weekresolver;

import java.util.Date;

/**
 * Result data from resolving a weeknumber by use of a specific catalogue code
 */
public class WeekResolverResult {
    // Calculated week number
    private int weekNumber;

    // Calculated year
    private int year;

    // Given cataloguecode, always in upper case
    private String catalogueCode;

    // Calculated weekcode
    private String weekCode;


    // The first possible date of release, adjusted for weeks into the future, shiftday and closing days
    // This is the date that is used to give the weeknumber and year - NOT the date that should be used
    // for other date fields, it only relates to the weekcode being calculated
    private Date date;

    public WeekResolverResult() {}

    private WeekResolverResult(Date date, int weekNumber, int year, String weekCode, String catalogueCode) {
        this.date = date;
        this.weekNumber=weekNumber;
        this.year=year;
        this.weekCode=weekCode;
        this.catalogueCode=catalogueCode;
    }

    public static WeekResolverResult create(Date date, int weekNumber, int year, String weekCode, String catalogueCode) {
        return new WeekResolverResult(date, weekNumber, year, weekCode, catalogueCode);
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public int getYear() {
        return year;
    }

    public Date getDate() {
        return date;
    }

    public String getCatalogueCode() {
        return catalogueCode;
    }

    public String getWeekCode() { return weekCode; }

}
