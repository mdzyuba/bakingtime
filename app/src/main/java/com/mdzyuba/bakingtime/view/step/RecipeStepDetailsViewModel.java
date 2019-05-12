package com.mdzyuba.bakingtime.view.step;

import android.app.Application;

import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.repository.LoadStepTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class RecipeStepDetailsViewModel extends AndroidViewModel {

    private MutableLiveData<Step> step;

    public RecipeStepDetailsViewModel(@NonNull Application application) {
        super(application);
        step = new MutableLiveData<>();
    }

    public void loadStep(int stepId) {
        LoadStepTask loadStepTask = new LoadStepTask(getApplication(), stepId, step);
        loadStepTask.execute();
    }

    public MutableLiveData<Step> getStep() {
        return step;
    }
}
