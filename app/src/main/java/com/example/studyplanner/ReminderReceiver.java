package com.example.studyplanner;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * Receives the alarm scheduled by {@link ReminderScheduler} and shows the
 * task reminder notification. Tapping the notification opens the task details.
 */
public class ReminderReceiver extends BroadcastReceiver {

    public static final String EXTRA_TASK_ID = "extra_task_id";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DUE_DATE = "extra_due_date";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper.ensureChannel(context);

        if (!SettingsManager.notificationsEnabled(context)) {
            return;
        }

        long taskId = intent.getLongExtra(EXTRA_TASK_ID, -1);
        String title = intent.getStringExtra(EXTRA_TITLE);
        long dueDate = intent.getLongExtra(EXTRA_DUE_DATE, 0);

        // Fall back to the database if the intent did not carry the details.
        if (title == null || dueDate <= 0) {
            Task task = TaskDbHelper.getInstance(context).getTask(taskId);
            if (task != null) {
                title = task.getTitle();
                dueDate = task.getDueDate();
            } else {
                title = context.getString(R.string.notification_title);
            }
        }

        Intent openIntent = new Intent(context, TaskDetailsActivity.class);
        openIntent.putExtra(TaskDetailsActivity.EXTRA_TASK_ID, taskId);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent contentIntent =
                PendingIntent.getActivity(context, (int) taskId, openIntent, flags);

        String dueText = dueDate > 0
                ? context.getString(R.string.notification_due, DateUtils.formatDateTime(dueDate))
                : "";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(title + "\n" + dueText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);

        try {
            NotificationManagerCompat.from(context).notify((int) taskId, builder.build());
        } catch (SecurityException ignored) {
            // Notification permission was denied by the user; nothing to show.
        }
    }
}
