package com.example.android.projectbakingapp.ui;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class RecipeWidgetRemoteViewsService extends RemoteViewsService{

    //for view factory
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new RecipeWidgetsRemoteViewsFactory(this.getApplicationContext(), intent));
    }
}
