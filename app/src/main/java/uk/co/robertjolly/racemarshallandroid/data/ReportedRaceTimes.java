package uk.co.robertjolly.racemarshallandroid.data;

public class ReportedRaceTimes {

    private RaceTimes raceTimes;
    private ReportedItems reportedItems;

    public ReportedRaceTimes() {
        raceTimes = new RaceTimes();
        reportedItems = new ReportedItems();
    }

    public RaceTimes getRaceTimes() {
        return raceTimes;
    }

    public ReportedItems getReportedItems() {
        return reportedItems;
    }
}
