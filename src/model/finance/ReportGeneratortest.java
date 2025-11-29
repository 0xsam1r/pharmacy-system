package model.finance;

public class ReportGeneratortest {
    public static void main(String[] args) {
        String date = "2025-11-23";

        ReportGenerator.generateSalesReport(date);
        ReportGenerator.generateProfitGraph(date); 
    }
}