package com.mdzyuba.bakingtime.repository;

import android.content.Context;
import android.os.AsyncTask;

import com.mdzyuba.bakingtime.model.Step;

import java.lang.ref.WeakReference;

import androidx.lifecycle.MutableLiveData;


public class LoadStepTask extends AsyncTask<Void, Void, Step> {
    private final MutableLiveData<Step> step;
    private final WeakReference<Context> contextWeakReference;
    private final int stepPk;

    public LoadStepTask(Context context, int stepPk, MutableLiveData<Step> step) {
        this.contextWeakReference = new WeakReference<>(context);
        this.stepPk = stepPk;
        this.step = step;
    }

    @Override
    protected Step doInBackground(Void... voids) {
        RecipeFactory factory = new RecipeFactory();

        Context context = contextWeakReference.get();
        if (context != null) {
            return factory.loadStep(context, stepPk);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Step step) {
        if (step != null) {
            this.step.postValue(step);
        }
    }
}
