package io.github.a2nr.jetpakcourse.widgetapp

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.net.toUri
import io.github.a2nr.jetpakcourse.R

/**
 * Implementation of App Widget functionality.
 */
class StackImageAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            appWidgetManager.updateAppWidget(
                appWidgetId,
                configAppWidget(
                    context,
                    appWidgetId
                )
            )

        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        intent?.run {
            when (action) {
                AppWidgetManager.ACTION_APPWIDGET_UPDATE ->
                    context?.let {
                        val id = AppWidgetManager.getInstance(context)
                            .getAppWidgetIds(
                                ComponentName(
                                    context,
                                    StackImageAppWidgetProvider::class.java
                                )
                            )
                        AppWidgetManager.getInstance(it)
                            .notifyAppWidgetViewDataChanged(
                                id,
                                R.id.StackViewFavorite
                            )
                    }
                else -> Log.i("onReceive", "Intent.action not found")
            }
        }
    }

    companion object {
        internal fun sendRefresh(context: Context?) {
            context?.let {
                Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).run {
                    component = ComponentName(
                        it
                        , StackImageAppWidgetProvider::class.java
                    )
                    it.sendBroadcast(this)
                }
            }
        }

        internal fun configAppWidget(
            context: Context,
            appWidgetId: Int
        ): RemoteViews {
            val intent = Intent(context, StackImageService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = this.toUri(Intent.URI_INTENT_SCHEME).toUri()
            }

            return RemoteViews(
                context.packageName,
                R.layout.stack_image_app_widget
            ).apply {
                setTextViewText(
                    R.id.appwidget_text, context.getString(
                        R.string.favorite_movie
                    )
                )
                setRemoteAdapter(R.id.StackViewFavorite, intent)
                setEmptyView(
                    R.id.StackViewFavorite,
                    R.id.empty_view
                )
            }

        }
    }
}

