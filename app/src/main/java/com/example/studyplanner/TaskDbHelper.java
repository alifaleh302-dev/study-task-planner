package com.example.studyplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Single SQLite helper shared by every activity of the application.
 *
 * <p>It creates and upgrades the {@code tasks} and {@code categories} tables and
 * exposes small, purpose-named methods for the CRUD operations used by the UI.
 * All queries that take user input use {@code selectionArgs} (parameter binding)
 * so values are never concatenated into raw SQL.</p>
 */
public class TaskDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "study_planner.db";
    private static final int DATABASE_VERSION = 1;

    // ---- tasks table ----
    public static final String TABLE_TASKS = "tasks";
    public static final String COL_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_DUE_DATE = "due_date";
    public static final String COL_PRIORITY = "priority";
    public static final String COL_CATEGORY_ID = "category_id";
    public static final String COL_IS_COMPLETED = "is_completed";
    public static final String COL_REMINDER_ENABLED = "reminder_enabled";
    public static final String COL_REMINDER_TIME = "reminder_time";
    public static final String COL_CREATED_AT = "created_at";

    // ---- categories table ----
    public static final String TABLE_CATEGORIES = "categories";
    public static final String COL_CAT_ID = "id";
    public static final String COL_CAT_NAME = "name";
    public static final String COL_CAT_COLOR = "color";

    private static final String CREATE_TASKS =
            "CREATE TABLE " + TABLE_TASKS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_TITLE + " TEXT NOT NULL, " +
                    COL_DESCRIPTION + " TEXT, " +
                    COL_DUE_DATE + " INTEGER, " +
                    COL_PRIORITY + " INTEGER, " +
                    COL_CATEGORY_ID + " INTEGER, " +
                    COL_IS_COMPLETED + " INTEGER DEFAULT 0, " +
                    COL_REMINDER_ENABLED + " INTEGER DEFAULT 0, " +
                    COL_REMINDER_TIME + " INTEGER, " +
                    COL_CREATED_AT + " INTEGER)";

    private static final String CREATE_CATEGORIES =
            "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                    COL_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_CAT_NAME + " TEXT NOT NULL, " +
                    COL_CAT_COLOR + " TEXT)";

    private static TaskDbHelper instance;

    private TaskDbHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** Returns the shared helper so every activity works with one database. */
    public static synchronized TaskDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new TaskDbHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TASKS);
        db.execSQL(CREATE_CATEGORIES);
        insertDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    /** Inserts the five default categories the first time the database is created. */
    private void insertDefaultCategories(SQLiteDatabase db) {
        String[][] defaults = {
                {"دراسة", "#1565C0"},
                {"عمل", "#6A1B9A"},
                {"شخصي", "#2E7D32"},
                {"صحة", "#C62828"},
                {"أخرى", "#616161"}
        };
        for (String[] row : defaults) {
            ContentValues values = new ContentValues();
            values.put(COL_CAT_NAME, row[0]);
            values.put(COL_CAT_COLOR, row[1]);
            db.insert(TABLE_CATEGORIES, null, values);
        }
    }

    // ---------------------------------------------------------------------
    // Task operations
    // ---------------------------------------------------------------------

    /** Inserts a new task and returns its generated id, or -1 on failure. */
    public long insertTask(Task task) {
        ContentValues values = taskToValues(task);
        values.put(COL_CREATED_AT, System.currentTimeMillis());
        return getWritableDatabase().insert(TABLE_TASKS, null, values);
    }

    /** Updates an existing task; returns the number of affected rows. */
    public int updateTask(Task task) {
        return getWritableDatabase().update(
                TABLE_TASKS,
                taskToValues(task),
                COL_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
    }

    /** Deletes a task by id; returns the number of affected rows. */
    public int deleteTask(long taskId) {
        return getWritableDatabase().delete(
                TABLE_TASKS,
                COL_ID + " = ?",
                new String[]{String.valueOf(taskId)});
    }

    /** Flips the completion flag of a task and returns the new value. */
    public boolean toggleCompleted(long taskId) {
        Task task = getTask(taskId);
        if (task == null) {
            return false;
        }
        boolean newState = !task.isCompleted();
        ContentValues values = new ContentValues();
        values.put(COL_IS_COMPLETED, newState ? 1 : 0);
        getWritableDatabase().update(
                TABLE_TASKS, values, COL_ID + " = ?",
                new String[]{String.valueOf(taskId)});
        return newState;
    }

    /** Reads one task by id, or {@code null} when it does not exist. */
    public Task getTask(long taskId) {
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_TASKS, null,
                COL_ID + " = ?", new String[]{String.valueOf(taskId)},
                null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursorToTask(cursor);
            }
        }
        return null;
    }

    /**
     * Returns tasks filtered by completion state.
     *
     * @param filter 0 = all, 1 = completed, 2 = not completed
     */
    public List<Task> getTasks(int filter) {
        String selection = null;
        switch (filter) {
            case 1:
                selection = COL_IS_COMPLETED + " = 1";
                break;
            case 2:
                selection = COL_IS_COMPLETED + " = 0";
                break;
            default:
                selection = null;
                break;
        }
        return queryTasks(selection, null, COL_DUE_DATE + " ASC");
    }

    /** Returns the next {@code limit} tasks that are not completed yet. */
    public List<Task> getUpcomingTasks(int limit) {
        String selection = COL_IS_COMPLETED + " = 0 AND " + COL_DUE_DATE + " >= ?";
        String[] args = {String.valueOf(System.currentTimeMillis())};
        return queryTasks(selection, args,
                COL_DUE_DATE + " ASC LIMIT " + limit);
    }

    /** Returns tasks whose due date falls inside the given day. */
    public List<Task> getTasksForDay(long dayMillis) {
        long start = startOfDay(dayMillis);
        long end = start + 24L * 60L * 60L * 1000L;
        return queryTasks(
                COL_DUE_DATE + " >= ? AND " + COL_DUE_DATE + " < ?",
                new String[]{String.valueOf(start), String.valueOf(end)},
                COL_DUE_DATE + " ASC");
    }

    /** Returns every task that belongs to a category. */
    public List<Task> getTasksByCategory(long categoryId) {
        return queryTasks(
                COL_CATEGORY_ID + " = ?",
                new String[]{String.valueOf(categoryId)},
                COL_DUE_DATE + " ASC");
    }

    /** Searches task titles containing {@code keyword} (case-insensitive). */
    public List<Task> searchTasks(String keyword) {
        return queryTasks(
                COL_TITLE + " LIKE ?",
                new String[]{"%" + keyword + "%"},
                COL_DUE_DATE + " ASC");
    }

    /** Total number of tasks. */
    public int getTaskCount() {
        return countTasks(null, null);
    }

    /** Number of completed (or not completed) tasks. */
    public int getCompletedCount(boolean completed) {
        return countTasks(COL_IS_COMPLETED + " = ?",
                new String[]{completed ? "1" : "0"});
    }

    // ---------------------------------------------------------------------
    // Category operations
    // ---------------------------------------------------------------------

    /** Returns every category with its number of linked tasks. */
    public List<Category> getCategoriesWithCounts() {
        String sql = "SELECT c." + COL_CAT_ID + ", c." + COL_CAT_NAME + ", c." + COL_CAT_COLOR +
                ", (SELECT COUNT(*) FROM " + TABLE_TASKS + " t WHERE t." + COL_CATEGORY_ID +
                " = c." + COL_CAT_ID + ") AS task_count" +
                " FROM " + TABLE_CATEGORIES + " c ORDER BY c." + COL_CAT_ID + " ASC";

        List<Category> categories = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                Category category = new Category(
                        cursor.getLong(0), cursor.getString(1), cursor.getString(2));
                category.setTaskCount(cursor.getInt(3));
                categories.add(category);
            }
        }
        return categories;
    }

    /** Returns categories without the task count, mainly to fill the spinner. */
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_CATEGORIES, null, null, null, null, null, COL_CAT_ID + " ASC")) {
            while (cursor.moveToNext()) {
                categories.add(new Category(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_CAT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CAT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CAT_COLOR))));
            }
        }
        return categories;
    }

    /** Reads a category name by id, or an empty string when not found. */
    public String getCategoryName(long categoryId) {
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_CATEGORIES, new String[]{COL_CAT_NAME},
                COL_CAT_ID + " = ?", new String[]{String.valueOf(categoryId)},
                null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        }
        return "";
    }

    /** Inserts a custom category and returns its id, or -1 on failure. */
    public long insertCategory(String name, String color) {
        ContentValues values = new ContentValues();
        values.put(COL_CAT_NAME, name);
        values.put(COL_CAT_COLOR, color);
        return getWritableDatabase().insert(TABLE_CATEGORIES, null, values);
    }

    // ---------------------------------------------------------------------
    // Internal helpers
    // ---------------------------------------------------------------------

    private ContentValues taskToValues(Task task) {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, task.getTitle());
        values.put(COL_DESCRIPTION, task.getDescription());
        values.put(COL_DUE_DATE, task.getDueDate());
        values.put(COL_PRIORITY, task.getPriority());
        values.put(COL_CATEGORY_ID, task.getCategoryId());
        values.put(COL_IS_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COL_REMINDER_ENABLED, task.isReminderEnabled() ? 1 : 0);
        values.put(COL_REMINDER_TIME, task.getReminderTime());
        return values;
    }

    private List<Task> queryTasks(String selection, String[] args, String orderBy) {
        List<Task> tasks = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_TASKS, null, selection, args, null, null, orderBy)) {
            while (cursor.moveToNext()) {
                tasks.add(cursorToTask(cursor));
            }
        }
        return tasks;
    }

    private int countTasks(String selection, String[] args) {
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_TASKS, new String[]{"COUNT(*)"}, selection, args,
                null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    private Task cursorToTask(Cursor cursor) {
        Task task = new Task();
        task.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
        task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
        task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)));
        task.setDueDate(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DUE_DATE)));
        task.setPriority(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRIORITY)));
        task.setCategoryId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CATEGORY_ID)));
        task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_COMPLETED)) == 1);
        task.setReminderEnabled(cursor.getInt(cursor.getColumnIndexOrThrow(COL_REMINDER_ENABLED)) == 1);
        task.setReminderTime(cursor.getLong(cursor.getColumnIndexOrThrow(COL_REMINDER_TIME)));
        task.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)));
        return task;
    }

    /** Kept public so the calendar screen can normalise a chosen date. */
    public static long startOfDay(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
