package DB;

import model.branch.Branch;
import model.finance.Treasury;
import model.finance.Transaction;

import java.util.List;

public class TestTreasury {
    public static void main(String[] args) {
        try {
            // أول حاجة: نجيب فرع موجود في الداتا بيز
            Branch branch = Branch.getBranchById("1"); // فرع رقم 1 مثلاً

            if (branch == null) {
                System.out.println("❌ مفيش فرع بالـ ID ده.");
                return;
            }

            // نعمل كائن Treasury للفرع
            Treasury treasury = new Treasury(1, 0, null, branch);

            // نجرب الدوال واحدة واحدة 👇
            System.out.println("===== 💰 بيانات اليوم للفرع: " + branch.getName() + " =====");
            System.out.println("إجمالي المبيعات: " + treasury.getTotalDailySales());
            System.out.println("إجمالي المشتريات: " + treasury.getTotalDailyPurchases());
            System.out.println("مرتجعات البيع: " + treasury.getTotalDailySaleReturns());
            System.out.println("مرتجعات الشراء: " + treasury.getTotalDailyPurchaseReturns());

            System.out.println("\n===== 📜 سجل المعاملات =====");
            List<Transaction> transactions = treasury.getTransactionsHistory();

            if (transactions.isEmpty()) {
                System.out.println("مفيش معاملات لليوم الحالي.");
            } else {
                for (Transaction t : transactions) {
                    System.out.println("-------------------------------------------------");
                    System.out.println("النوع: " + t.getType());
                    System.out.println("المبلغ: " + t.getAmountOfMoney());
                    System.out.println("التاريخ: " + t.getDateAndTime());
                    System.out.println("رقم الفاتورة: " + t.getInvoice().getInvoiceID());
                    System.out.println("الفرع: " + t.getInvoice().getBranch().getName());
                }
            }

        } catch (Exception e) {
            System.err.println("⚠️ حصل خطأ أثناء الاختبار: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
