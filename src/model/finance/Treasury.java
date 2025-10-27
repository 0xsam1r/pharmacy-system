package model.finance;

//import model.branch.Branch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Treasury {
    private int id;
    private double currentBalance;
    private LocalDate lastUpdatedDate;
    //private Branch branch;

    public Treasury(int id, double currentBalance, LocalDate lastUpdatedDate/*, Branch branch*/) {
        this.id = id;
        this.currentBalance = currentBalance;
        this.lastUpdatedDate = lastUpdatedDate;
        //this.branch = branch;
    }
    public Treasury() {
        this.id = 0;
        this.currentBalance = 0;
        this.lastUpdatedDate = null;
        //this.branch = null;
    }
    public Treasury(Treasury t) {
        this.id = t.id;
        this.currentBalance = t.currentBalance;
        this.lastUpdatedDate = t.lastUpdatedDate;
        //this.branch = t.branch;
    }

    public void addIncome() {
        // Stub: add income (would take amount in real impl)
        System.out.println("Adding income to treasury.");
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public double getTotalDailyPurchaseReturns() {
        // Stub
        return 0.0;
    }

    public double getTotalDailySaleReturns() {
        // Stub
        return 0.0;
    }

    public double getTotalDailySales() {
        // Stub
        return 0.0;
    }

    public double getTotalDailyPurchases() {
        // Stub
        return 0.0;
    }

    public List<Transaction> getTransactionsHistory() {
        // Stub
        return new ArrayList<>();
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public void setCurrentBalance(double currentBalance) { this.currentBalance = currentBalance; }
    public LocalDate getLastUpdatedDate() { return lastUpdatedDate; }
    public void setLastUpdatedDate(LocalDate lastUpdatedDate) { this.lastUpdatedDate = lastUpdatedDate; }
//    public Branch getBranch() { return branch; }
//    public void setBranch(Branch branch) { this.branch = branch; }
}