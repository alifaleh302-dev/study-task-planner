package com.example.studyplanner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Schedules and cancels the local reminders stored with each task using
 * {@link AlarmManager}. The alarm fires a {@link ReminderReceiver} that shows
 * the notification, so the reminder still works while the app is closed.
 *
 * <p>The first version uses inexact alarms ({@code set()}) to keep the sample
 * simple and avoid exact-alarm restrictions on recent Android versions.</p>
 */
public final class ReminderScheduler {

    private ReminderScheduler() {
    }

    /** Schedules a reminder for the task, replacing any existing one. */
    public static void schedule(Context context, Task task) {
        if (task.getReminderTime() <= 0) {
            return;
        }
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }
        PendingIntent pendingIntent =
                buildPendingIntent(context, task.getId(), task.getTitle(), task.getDueDate());
        // An alarm in the past is scheduled right away so the user still sees it.
        long triggerAt = Math.max(task.getReminderTime(), System.currentTimeMillis() + 1000L);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
    }

    /** Cancels the reminder linked to a task. */
    public static void cancel(Context context, long taskId) {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }
        // The title/due date are not needed to cancel, but the PendingIntent must
        // match the one used when scheduling, so the same request code is reused.
        PendingIntent pendingIntent = buildPendingIntent(context, taskId, null, 0);
        alarmManager.cancel(pendingIntent);
    }

    private static PendingIntent buildPendingIntent(
            Context context, long taskId, String title, long dueDate) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(ReminderReceiver.EXTRA_TASK_ID, taskId);
        intent.putExtra(ReminderReceiver.EXTRA_TITLE, title);
        intent.putExtra(ReminderReceiver.EXTRA_DUE_DATE, dueDate);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        // The task id as request code keeps every reminder independent.
        return PendingIntent.getBroadcast(context, (int) taskId, intent, flags);
    }
}
