# منظم الدراسة والمهام — Study & Task Planner

تطبيق أندرويد محلي لتنظيم المهام الدراسية والشخصية، مبني بـ **Java** و**XML** على
نمط Android الكلاسيكي، بدون Kotlin ولا Jetpack Compose ولا خادم خارجي.
يحفظ التطبيق بياناته محليًا في **SQLite** باستخدام `SQLiteOpenHelper`، ويعمل
بالكامل دون اتصال بالإنترنت.

---

## 1. المزايا

- إضافة المهام الدراسية والشخصية وتعديلها وحذفها.
- تحديد تاريخ ووقت الاستحقاق عبر `DatePickerDialog` و`TimePickerDialog`.
- تصنيف المهمة حسب الفئة (دراسة، عمل، شخصي، صحة، أخرى) وإضافة فئات مخصصة.
- تحديد الأولوية: منخفضة، متوسطة، عالية.
- تغيير حالة المهمة إلى مكتملة أو غير مكتملة.
- عرض جميع المهام مع تصفية (الكل / المكتملة / غير المكتملة).
- عرض المهام حسب اليوم عبر التقويم.
- البحث في عناوين المهام.
- إحصائيات الإنجاز وتوزيع المهام حسب الفئة.
- إعدادات محفوظة عبر `SharedPreferences` (الإشعارات، التذكير الافتراضي، المظهر الداكن).
- تذكيرات محلية عبر `AlarmManager` و`NotificationManager` و`BroadcastReceiver`.
- واجهات عربية تدعم اتجاه الكتابة من اليمين إلى اليسار (RTL).

---

## 2. البنية

```
app/src/main/java/com/example/studyplanner/
    SplashActivity.java        MainActivity.java          AllTasksActivity.java
    AddTaskActivity.java       TaskDetailsActivity.java   EditTaskActivity.java
    CalendarActivity.java      CategoriesActivity.java    CategoryTasksActivity.java
    SearchActivity.java        StatisticsActivity.java    SettingsActivity.java
    AboutActivity.java

    BaseTaskFormActivity.java   CategorySpinnerAdapter.java
    Task.java                   Category.java              TaskDbHelper.java
    TaskAdapter.java            CategoryAdapter.java        ReminderReceiver.java
    ReminderScheduler.java      NotificationHelper.java     DateUtils.java
    DateTimePicker.java         SettingsManager.java        StudyPlannerApp.java

app/src/main/res/layout/
    activity_splash.xml ... activity_about.xml  (13 ملفات)
    item_task.xml   item_category.xml
```

تتشارك الأنشطة كلها قاعدة بيانات واحدة عبر `TaskDbHelper.getInstance(context)`.

> ملاحظة تعليمية: توجد أحيانًا مكوّنات مساعدة صغيرة إضافية (مثل
> `BaseTaskFormActivity` لشاشتي الإضافة والتعديل، و`ReminderScheduler` للتذكيرات)
> لتجنّب تكرار نفس الكود، مع الحفاظ على الفصل الواضح بين الأنشطة الثلاثة عشر.

---

## 3. قاعدة البيانات

جدول `tasks`:

| الحقل | النوع |
|---|---|
| id | INTEGER PRIMARY KEY AUTOINCREMENT |
| title | TEXT NOT NULL |
| description | TEXT |
| due_date | INTEGER (epoch millis) |
| priority | INTEGER |
| category_id | INTEGER |
| is_completed | INTEGER (0/1) |
| reminder_enabled | INTEGER (0/1) |
| reminder_time | INTEGER |
| created_at | INTEGER |

جدول `categories`:

| الحقل | النوع |
|---|---|
| id | INTEGER PRIMARY KEY AUTOINCREMENT |
| name | TEXT NOT NULL |
| color | TEXT |

تُضاف الفئات الافتراضية الخمس عند أول إنشاء لقاعدة البيانات. تستخدم جميع
الاستعلامات التي تعتمد على إدخال المستخدم معاملات (`selectionArgs`) لتفادي حقن SQL.

---

## 4. البناء والتشغيل

### باستخدام Android Studio
1. `File > Open` واختر مجلد المشروع.
2. انتظر مزامنة Gradle.
3. شغّل التطبيق على جهاز أو محاكي (minSdk 21).

### من سطر الأوامر
```bash
export ANDROID_SDK_ROOT=/path/to/android-sdk
./gradlew :app:assembleDebug
```
الناتج: `app/build/outputs/apk/debug/app-debug.apk`.

المشروع مُهيّأ بـ Gradle 8.x و Android Gradle Plugin 8.5.2 و Java 17.

---

## 5. ملاحظات تقنية

- التذكيرات غير دقيقة (`AlarmManager.set`) في هذا الإصدار لتبسيط المشروع وتفادي
  قيود التنبيهات الدقيقة في إصدارات أندرويد الحديثة.
- صلاحية `POST_NOTIFICATIONS` تُطلب عند تفعيل الإشعارات على Android 13 (API 33) فأعلى.
- المظهر الداكن اختياري ويُطبَّق عند اختياره من الإعدادات.

---

## 6. توثيق الكلاسات الرئيسية

- `TaskDbHelper`: إنشاء قاعدة البيانات والجداول، وعمليات الإضافة والتعديل والحذف
  والاستعلام (المهام، الفئات، البحث، الإحصائيات، مهام اليوم).
- `Task`: نموذج بيانات المهمة. `Category`: نموذج بيانات الفئة.
- `TaskAdapter` / `CategoryAdapter`: عرض القوائم في `RecyclerView`.
- `BaseTaskFormActivity`: المنطق المشترك لشاشتي الإضافة والتعديل (المنتقيان،
  التحقق من المدخلات، قراءة النموذج).
- `ReminderScheduler` / `ReminderReceiver` / `NotificationHelper`: جدولة التذكير
  وإلغاؤه وعرض الإشعار.
- `SettingsManager`: حفظ الإعدادات البسيطة.
- `DateUtils` / `DateTimePicker`: تنسيق التواريخ وفتح منتقيات التاريخ والوقت.
