# 📂 Complete File Structure - هيكل الملفات الكامل

## اللهم صلِّ وسلم وبارك على نبينا محمد ﷺ

---

## 📁 الملفات الجديدة المضافة

```
pharmacy-system/
│
├── 📄 FINAL_STATUS.md                          ← الحالة النهائية للمشروع
├── 📄 PROJECT_SUMMARY.md                       ← ملخص المشروع
├── 📄 NEW_FEATURES_README.md                   ← دليل البداية السريعة
├── 📄 GUI_AND_EXCEPTION_HANDLING_GUIDE.md      ← دليل شامل بالإنجليزي
├── 📄 الدليل_الكامل_بالعربي.md                 ← دليل شامل بالعربي
├── 📄 PRESENTATION_GUIDE.md                    ← دليل العرض التقديمي
├── 📄 FILE_STRUCTURE.md                        ← هذا الملف
├── 💻 run_gui.bat                              ← ملف تشغيل سريع
│
├── 📁 src/
│   │
│   ├── 📁 exceptions/                          ← ✅ Exception Handling
│   │   ├── 📄 DatabaseException.java           (Custom exception for database errors)
│   │   ├── 📄 ValidationException.java         (Custom exception for validation errors)
│   │   ├── 📄 InsufficientStockException.java  (Custom exception for stock issues)
│   │   ├── 📄 DuplicateEntryException.java     (Custom exception for duplicates)
│   │   └── 📄 EntityNotFoundException.java     (Custom exception for not found)
│   │
│   ├── 📁 util/                                ← ✅ Utility Classes
│   │   ├── 📄 ExceptionLogger.java             (Centralized exception logging)
│   │   ├── 📄 AlertHelper.java                 (Standardized alert dialogs)
│   │   ├── 📄 Validator.java                   (Input validation utilities)
│   │   ├── 📄 DateFormatter.java               (Date formatting utilities)
│   │   └── 📄 ConfigManager.java               (Configuration management)
│   │
│   └── 📁 gui/                                 ← ✅ JavaFX GUI Application
│       │
│       ├── 📄 PharmacyApp.java                 ⭐ MAIN CLASS - Start here!
│       │
│       ├── 📁 css/
│       │   └── 📄 styles.css                   (Modern CSS styling for entire app)
│       │
│       ├── 📁 fxml/                            ← FXML Views (9 files)
│       │   ├── 📄 LoginScreen.fxml             (Login interface)
│       │   ├── 📄 Dashboard.fxml               (Main dashboard)
│       │   ├── 📄 ProductsView.fxml            (Products management)
│       │   ├── 📄 CustomersView.fxml           (Customers management)
│       │   ├── 📄 ReportsView.fxml             (Reports and analytics)
│       │   ├── 📄 InventoryView.fxml           (Inventory - placeholder)
│       │   ├── 📄 SalesView.fxml               (Sales - placeholder)
│       │   ├── 📄 EmployeesView.fxml           (Employees - placeholder)
│       │   └── 📄 SettingsView.fxml            (Settings - placeholder)
│       │
│       └── 📁 controllers/                     ← Controllers (5 files)
│           ├── 📄 LoginController.java         (Login logic + authentication)
│           ├── 📄 DashboardController.java     (Dashboard logic + navigation)
│           ├── 📄 ProductsController.java      (Products CRUD + validation)
│           ├── 📄 CustomersController.java     (Customers CRUD + validation)
│           └── 📄 ReportsController.java       (Reports generation)
│
├── 📁 logs/                                    ← Created at runtime
│   └── 📄 pharmacy_errors.log                  (Exception logs)
│
└── 📄 pharmacy.properties                      ← Created at runtime (config)
```

---

## 🎯 الملفات الأساسية (Must Know)

### للتشغيل:
1. **`src/gui/PharmacyApp.java`** - Main class
2. **`run_gui.bat`** - Quick launcher

### للفهم:
1. **`FINAL_STATUS.md`** - فهم شامل
2. **`PRESENTATION_GUIDE.md`** - للعرض التقديمي

### للكود:
1. **Exception Handling:**
   - `src/exceptions/*.java` (5 files)
   - `src/util/ExceptionLogger.java` (1 file)

2. **JavaFX GUI:**
   - `src/gui/PharmacyApp.java` (Main)
   - `src/gui/fxml/*.fxml` (9 files)
   - `src/gui/controllers/*.java` (5 files)
   - `src/gui/css/styles.css` (1 file)

---

## 📊 Statistics

### Files Added:
- **Documentation:** 7 files
- **Java Classes:** 15 files
- **FXML Views:** 9 files
- **CSS:** 1 file
- **Scripts:** 1 file
- **Total:** 33 new files

### Lines of Code:
- **Exception Handling:** ~500 lines
- **Utilities:** ~800 lines
- **GUI Classes:** ~1,200 lines
- **FXML:** ~1,500 lines
- **CSS:** ~400 lines
- **Documentation:** ~2,000 lines
- **Total:** ~6,400 lines

### Packages:
- `exceptions` → 5 classes
- `util` → 5 classes
- `gui` → 1 main + 5 controllers
- `gui.fxml` → 9 views
- `gui.css` → 1 stylesheet

---

## 🎨 GUI Structure Breakdown

### Views Hierarchy:
```
Login Screen
    ↓
Dashboard (Main Window)
    ├── Products View
    ├── Customers View
    ├── Inventory View (placeholder)
    ├── Sales View (placeholder)
    ├── Employees View (placeholder)
    ├── Reports View
    └── Settings View (placeholder)
```

### Navigation Flow:
```
Start → Login → Dashboard
                    ↓
        Sidebar Navigation
                    ↓
        Switch Between Views
```

---

## 🔧 Class Dependencies

### Exception Handling Flow:
```
Application Code
    ↓
Try-Catch Blocks
    ↓
Custom Exceptions (DatabaseException, ValidationException, etc.)
    ↓
ExceptionLogger
    ↓
logs/pharmacy_errors.log
```

### GUI Flow:
```
PharmacyApp (Main)
    ↓
LoginScreen.fxml + LoginController
    ↓
Dashboard.fxml + DashboardController
    ↓
Various Views.fxml + ViewControllers
    ↓
Database (via DB_operation)
```

---

## 📂 Important Directories

### Source Code:
```
src/
├── exceptions/    ← Exception handling
├── util/          ← Utilities
├── gui/           ← GUI application
├── DB/            ← Database operations (existing)
├── model/         ← Data models (existing)
└── enums/         ← Enumerations (existing)
```

### Resources:
```
src/gui/
├── css/           ← Stylesheets
└── fxml/          ← View definitions
```

### Runtime:
```
logs/              ← Error logs
reports/           ← Generated reports (existing)
```

---

## 🎯 File Purposes

### Exception Handling:
| File | Purpose |
|------|---------|
| `DatabaseException.java` | Database operation errors |
| `ValidationException.java` | Input validation errors |
| `InsufficientStockException.java` | Stock availability errors |
| `DuplicateEntryException.java` | Duplicate data errors |
| `EntityNotFoundException.java` | Entity not found errors |

### Utilities:
| File | Purpose |
|------|---------|
| `ExceptionLogger.java` | Log all exceptions to file |
| `AlertHelper.java` | Show standardized alerts |
| `Validator.java` | Validate user inputs |
| `DateFormatter.java` | Format dates consistently |
| `ConfigManager.java` | Manage app configuration |

### Controllers:
| File | Purpose |
|------|---------|
| `LoginController.java` | Handle login & authentication |
| `DashboardController.java` | Main dashboard & navigation |
| `ProductsController.java` | Products CRUD operations |
| `CustomersController.java` | Customers CRUD operations |
| `ReportsController.java` | Generate reports |

---

## 🚀 Execution Flow

### 1. Application Start:
```
run_gui.bat OR IDE Run
    ↓
gui.PharmacyApp.main()
    ↓
Load LoginScreen.fxml
    ↓
Show Login Window
```

### 2. After Login:
```
LoginController.handleLogin()
    ↓
Authenticate User (DB)
    ↓
Load Dashboard.fxml
    ↓
Show Main Window
```

### 3. Navigation:
```
User clicks sidebar button
    ↓
DashboardController.showXXXView()
    ↓
Load corresponding FXML
    ↓
Display in content area
```

### 4. Error Handling:
```
Any exception occurs
    ↓
Caught by try-catch
    ↓
ExceptionLogger.logException()
    ↓
Show user-friendly message
    ↓
Continue execution
```

---

## 📖 Documentation Files

### Start Here:
1. **FINAL_STATUS.md** - Overall status
2. **PROJECT_SUMMARY.md** - Quick overview

### Guides:
3. **NEW_FEATURES_README.md** - Quick start
4. **GUI_AND_EXCEPTION_HANDLING_GUIDE.md** - Technical guide (English)
5. **الدليل_الكامل_بالعربي.md** - Complete guide (Arabic)

### Reference:
6. **PRESENTATION_GUIDE.md** - For presentation
7. **FILE_STRUCTURE.md** - This file

---

## 🎓 Learning Path

### To Understand Exception Handling:
1. Read `exceptions/DatabaseException.java`
2. Read `util/ExceptionLogger.java`
3. See usage in `gui/controllers/ProductsController.java`
4. Check `logs/pharmacy_errors.log`

### To Understand GUI:
1. Read `gui/PharmacyApp.java` (Entry point)
2. Read `gui/fxml/LoginScreen.fxml` (Simple view)
3. Read `gui/controllers/LoginController.java` (Simple controller)
4. Read `gui/fxml/Dashboard.fxml` (Complex view)
5. Read `gui/controllers/DashboardController.java` (Complex controller)

---

## ✅ Checklist Before Running

- [ ] MySQL server is running
- [ ] Database `PMS` exists and is populated
- [ ] JavaFX SDK is downloaded
- [ ] VM options are configured correctly
- [ ] Main class is set to `gui.PharmacyApp`
- [ ] All source files are compiled
- [ ] This file structure is understood 😊

---

## 🎉 You're All Set!

**Everything is organized and documented.**
**Just follow the files in order.**
**Good luck! 💪**

**صلوا على النبي ﷺ**

---

Made with ❤️ for success
December 2025
