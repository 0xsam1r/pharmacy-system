package model.invoices;
import java.time.LocalDateTime;

/*=============================================
###### Written By Samir Ahmed (0xsam1r) #######
Email: 0xsam1r@proton.me
=============================================*/

/*
* ##########################################################
* An invoice shows what was sold, how much, and when —
* like a printed receipt you get after buying medicine.
* ##########################################################
*/



public class Invoice {
    protected String BranchID; // BranchID are same for all program
    protected int InvoiceID;
    protected LocalDateTime date; // using unix time or what ? unix good for api and time calculations
    protected double totalPrice;
    // protected double totalAmount; // one is good
    // Protected SalesInvoice;
}
