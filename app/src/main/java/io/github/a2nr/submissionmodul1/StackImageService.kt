package io.github.a2nr.submissionmodul1

import android.content.Intent
import android.widget.RemoteViewsService

class StackImageService : RemoteViewsService(){
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory{
        return StackImageViewFactory(this.applicationContext)
    }
}