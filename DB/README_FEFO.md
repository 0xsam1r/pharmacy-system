# FEFO Batch Management System
## First Expire First Out (الأقدم انتهاءً أولاً)

## 📋 المفهوم الأساسي

احنا عملنا نظام إدارة Batches بحيث:
1. **كل Batch** ليها كمية معينة (`Quantaty`) وتاريخ انتهاء (`expire_date`)
2. **لما نبيع منتج**، بنسحب من الـBatch اللي **هتفسد الأول** (أقرب تاريخ انتهاء)
3. **الكمية الكلية** في الـInventory بتتحدّث تلقائياً مع تحديث الـBatches

---

## 🗂️ هيكل البيانات

### جدول `batch`
```sql
CREATE TABLE batch (
    Batch_number VARCHAR(14),
    cost FLOAT,
    expire_date DATE,
    Quantaty INT,           -- الكمية الموجودة
    Product_parcode VARCHAR(14),
    PRIMARY KEY (Batch_number, Product_parcode)
);
```

### جدول `inventory_has_product`
```sql
CREATE TABLE inventory_has_product (
    Inventory_ID INT,
    Product_parcode VARCHAR(14),
    Quntaty FLOAT,          -- مجموع كل الكميات في الـBatches
    reordr_level INT,
    PRIMARY KEY (Inventory_ID, Product_parcode)
);
```

---

## 🔄 كيفية العمل (FEFO Logic)

### مثال: Panadol (باراكود: 62230000000123)

**البatches الموجودة:**
| Batch Number   | Expire Date | Quantity | Status |
|---------------|-------------|----------|--------|
| BN202501014   | 2026-03-15  | 40       | ينتهي أولاً |
| BN202501001   | 2026-06-30  | 50       | ينتهي ثانياً |
| BN202501015   | 2026-09-20  | 30       | ينتهي أخيراً |
| **Total**     | -           | **120**  | - |

**لما نبيع 60 وحدة:**
1. ✅ نأخذ 40 من `BN202501014` (ينتهي في مارس) → باقي 0
2. ✅ نأخذ 20 من `BN202501001` (ينتهي في يونيو) → باقي 30
3. ❌ `BN202501015` لا يُمس (لأننا أخذنا ما يكفي)

**النتيجة:**
| Batch Number   | Quantity After Sale |
|---------------|---------------------|
| BN202501014   | 0 (نفذ)             |
| BN202501001   | 30                  |
| BN202501015   | 30                  |
| **Total**     | **60**              |

الـInventory هيتحدّث تلقائياً: `120 - 60 = 60`

---

## 💻 استخدام الكود

### 1. تقليل الكمية (FEFO)
```java
import DB.BatchManager;

// تقليل 60 وحدة من Panadol في المخزن رقم 1
boolean success = BatchManager.reduceQuantityFromBatches(
    "62230000000123",  // Product barcode
    60,                 // Quantity to reduce
    1                   // Inventory ID
);

if (success) {
    System.out.println("تم البيع بنجاح مع تطبيق FEFO");
} else {
    System.out.println("كمية غير كافية أو خطأ في البيع");
}
```

### 2. الحصول على Batches لمنتج معين
```java
import DB.BatchManager;
import java.util.List;

List<BatchManager.Batch> batches = 
    BatchManager.getBatchesForProduct("62230000000123");

for (BatchManager.Batch batch : batches) {
    System.out.printf("Batch: %s, Qty: %d, Expires: %s%n",
        batch.getBatchNumber(),
        batch.getQuantity(),
        batch.getExpireDate()
    );
}
```

### 3. إضافة كمية لـBatch معين
```java
// إضافة 50 وحدة للbatch BN202501001
boolean success = BatchManager.addQuantityToBatch(
    "BN202501001",     // Batch number
    "62230000000123",  // Product barcode
    50,                 // Quantity to add
    1                   // Inventory ID
);
```

### 4. الحصول على إجمالي الكمية المتاحة
```java
int totalQty = BatchManager.getTotalAvailableQuantity("62230000000123");
System.out.println("Total available: " + totalQty);
```

---

## 🧪 اختبار النظام

### باستخدام الـSQL Test Script
```bash
# من MySQL Workbench أو command line
mysql -u root -p pms < DB/test_fefo_logic.sql
```

هيعرض لك:
- ✅ الـBatches قبل البيع
- ✅ محاكاة البيع
- ✅ الـBatches بعد البيع
- ✅ التحقق من تطابق الأرقام

---

## 📊 البيانات المُدخلة

أضفنا batches متعددة لكل منتج:

| Product       | Number of Batches | Total Quantity |
|--------------|-------------------|----------------|
| Panadol      | 3                 | 120            |
| Augmentin    | 2                 | 60             |
| Cataflam     | 2                 | 90             |
| Brufen       | 2                 | 100            |
| Zyrtec       | 2                 | 80             |
| Vitamin C    | 2                 | 50             |
| وغيرها...    | -                 | -              |

---

## ⚠️ نقاط مهمة

1. **Transaction Safety**: كل العمليات بتتم في transaction عشان نضمن consistency
2. **FEFO Automatic**: الترتيب بيحصل تلقائياً بناءً على `expire_date`
3. **Inventory Sync**: الـInventory بيتحدّث تلقائياً مع الـBatches
4. **Zero Check**: لو batch خلصت، بتفضل موجودة بس بكمية 0
5. **Insufficient Stock**: لو الكمية مش كافية، العملية بترجع false والـtransaction بيترجع

---

## 🔧 الملفات المهمة

| File | Description |
|------|-------------|
| `BatchManager.java` | الكلاس اللي بيدير الـBatches بمنطق FEFO |
| `data insertion2.sql` | البيانات الأساسية مع batches متعددة |
| `test_fefo_logic.sql` | سكريبت لاختبار الـFEFO logic |
| `README_FEFO.md` | هذا الملف |

---

## 📝 ملاحظات للتطوير

- يمكن إضافة **alerts** للbatches اللي قربت تفسد (مثلاً أقل من 3 شهور)
- يمكن عمل **reports** عن الـbatches المُستنفذة
- يمكن إضافة **validation** للتأكد من عدم البيع بعد تاريخ الانتهاء
- يمكن ربط الـBatchManager بالـSales system

---

## 🎯 الخلاصة

النظام دلوقتي بيدعم:
✅ إدارة Batches متعددة لنفس المنتج
✅ FEFO (البيع من الأقدم انتهاءً)
✅ تحديث تلقائي للـInventory
✅ Transaction safety
✅ Error handling and logging
