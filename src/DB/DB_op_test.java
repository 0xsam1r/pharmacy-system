package DB;

import DB.DB_operation;
import model.branch.Branch;
import model.people.Customer;
import model.people.Employee;
import model.people.UserAccount;

public class DB_op_test {

    public static void main(String[] args) {
        
        System.out.println("--- بدء اختبار عمليات قاعدة البيانات ---");

        // اختبار إضافة عميل
        testAddCustomer();
        
        System.out.println("\n----------------------------------\n");

        
        testAddEmployeeWithIntegratedAccount();
        
        System.out.println("\n--- انتهاء الاختبارات ---");
    }

    private static void testAddCustomer() {
        System.out.println("--- اختبار إضافة عميل ---");
        
        // إنشاء كائن عميل وهمي جديد
        Customer c1 = new Customer("C001", "Ahmed Ali", "01012345678", 50.0);
        
        boolean success = DB_operation.add_Customer(c1);
        
        if (success) {
            System.out.println("[نجاح]: تم إضافة العميل C001 بنجاح.");
        } else {
            System.out.println("[فشل]: حدث خطأ أثناء إضافة العميل C001 (قد يكون موجوداً مسبقاً).");
        }
        System.out.println("\nمحاولة إضافة العميل C001 مرة أخرى لاختبار التحقق من الوجود:");
        boolean successAgain = DB_operation.add_Customer(c1);
        if (!successAgain) {
             System.out.println("[نجاح في الـ Validation]: تم منع إضافة العميل المكرر C001 بنجاح.");
        }
    }

    private static void testAddEmployeeWithIntegratedAccount() {
        System.out.println("--- اختبار إضافة موظف (مع حساب مستخدم مدمج) ---");
        Branch b1 = new Branch();
        b1.setId("1"); 

        UserAccount ua1 = new UserAccount("mona.h", "123", null);

        // إنشاء كائن موظف وهمي جديد
        Employee emp1 = new Employee();
        emp1.setId("E0010");
        emp1.setName("Mona Hassan");
        emp1.setPhone("01198765432");
        emp1.setSalary(5000);
        emp1.setStartDate("2023-01-15"); 
        emp1.setBranch(b1);
        emp1.setAccount(ua1);
        
        boolean success = DB_operation.add_Employee(emp1);
        
        if (success) {
            System.out.println("[نجاح]: تم إضافة الموظف E001 بنجاح في الفرع 1 مع بيانات الحساب.");
        } else {
            System.out.println("[فشل]: حدث خطأ أثناء إضافة الموظف E001 (قد يكون موجوداً مسبقاً أو معرف الفرع غير صحيح).");
        }

        // محاولة إضافة نفس الموظف مرة أخرى لاختبار الـ Validation
        System.out.println("\nمحاولة إضافة الموظف E001 مرة أخرى لاختبار التحقق من الوجود:");
        boolean successAgain = DB_operation.add_Employee(emp1);
        if (!successAgain) {
             System.out.println("[نجاح في الـ Validation]: تم منع إضافة الموظف المكرر E001 بنجاح.");
        }
    }
}
