package model.people;

import model.branch.Branch;

public class Employee extends Person {

    private UserAccount account;
    private int salary;
    private String startDate;
    private Branch branch;

    public Employee(String id, String name, String phone, UserAccount account, int salary, String startDate, Branch branch) {
        super(id, name, phone);
        this.account = account;
        this.salary = salary;
        this.startDate = startDate;
        this.branch = branch;
    }

    public Employee(Employee e) {
        super(e.id, e.name, e.phone);
        this.account = e.account;
        this.salary = e.salary;
        this.startDate = e.startDate;
        this.branch = e.branch;
    }

    public Employee() {
        super();
        this.account = null;
        this.salary = 0;
        this.startDate = "";
        this.branch = null;
    }

    public UserAccount getAccount() {
        return account;
    }

    public void setAccount(UserAccount account) {
        this.account = account;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}
