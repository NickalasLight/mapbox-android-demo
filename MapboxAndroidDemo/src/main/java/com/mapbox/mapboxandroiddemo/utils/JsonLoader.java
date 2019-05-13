package com.mapbox.mapboxandroiddemo.utils;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.lang.ref.WeakReference;

import timber.log.Timber;

/**
 * This class is a helper class to asynchrously load data
 * while holding a weak reference to a callback.
 * T data that is loaded
 * @param <T> data to be loaded
 */
public class JsonLoader<T> extends AsyncTask<Void, Void, T> {

  public interface DataUi<T> {
    T loadData() throws IOException;

    void onDataLoaded(T data);
  }

  private WeakReference<DataUi> weakReference;

  public JsonLoader(DataUi ui) {
    this.weakReference = new WeakReference<>(ui);
  }

  @Override
  protected T doInBackground(Void... voids) {
    try {
      DataUi ui = weakReference.get();
      if (ui != null) {
        return (T) ui.loadData();
      }
    } catch (Exception exception) {
      Timber.d("Exception Loading data: %s", exception.toString());
    }
    return null;
  }

  @Override
  protected void onPostExecute(@Nullable T data) {
    super.onPostExecute(data);
    DataUi ui = weakReference.get();
    if (ui != null && data != null) {
      ui.onDataLoaded(data);
    }
  }
}
