package model.invoices;

import model.people.Customer;
import java.util.List;

public class SaleInvoice {
    private Customer customer;
    private double discount;
    private List<InvoiceItem> invoiceItems;
    private double supTotal;
}
