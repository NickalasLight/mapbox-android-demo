package com.mapbox.mapboxandroiddemo.examples.styles;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxandroiddemo.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Scanner;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_LEFT;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_RIGHT;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_TOP;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_JUSTIFY_AUTO;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textJustify;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textRadialOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textVariableAnchor;


/**
 * To increase the chance of high-priority labels staying visible, provide the map
 * renderer a list of preferred text anchor positions via
 * {@link com.mapbox.mapboxsdk.style.layers.PropertyFactory#textVariableAnchor(String[])}.
 */
public class VariableLabelPlacementActivity extends AppCompatActivity {

  private static final String GEOJSON_SRC_ID = "poi_source_id";
  private static final String POI_LABELS_LAYER_ID = "poi_labels_layer_id";
  private MapView mapView;
  private MapboxMap mapboxMap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Mapbox access token is configured here. This needs to be called either in your application
    // object or in the same activity which contains the mapview.
    Mapbox.getInstance(this, getString(R.string.access_token));

    // This contains the MapView in XML and needs to be called after the access token is configured.
    setContentView(R.layout.activity_style_variable_text_placement);

    mapView = findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        mapboxMap.setStyle(new Style.Builder().fromUrl(Style.LIGHT)
                .withSource(new GeoJsonSource(GEOJSON_SRC_ID))
                // Adds a SymbolLayer to display POI labels
                .withLayer(new SymbolLayer(POI_LABELS_LAYER_ID, GEOJSON_SRC_ID)
                    .withProperties(
                        //iconImage(POI_ICON),
                        textField(get("description")),
                        textSize(17f),
                        textColor(Color.RED),
                        textVariableAnchor(
                            new String[]{TEXT_ANCHOR_TOP, TEXT_ANCHOR_BOTTOM, TEXT_ANCHOR_LEFT, TEXT_ANCHOR_RIGHT}),
                        textJustify(TEXT_JUSTIFY_AUTO),
                        textRadialOffset(0.5f))),
            new Style.OnStyleLoaded() {
              @Override
              public void onStyleLoaded(@NonNull final Style style) {
                VariableLabelPlacementActivity.this.mapboxMap = mapboxMap;
                new LoadGeoJson(style,VariableLabelPlacementActivity.this).execute();
              }
            });
      }
    });
  }

  public void onDataLoaded(Style style, FeatureCollection featureCollection) {
    if (mapboxMap != null && style != null) {
      GeoJsonSource source = style.getSourceAs(GEOJSON_SRC_ID);
      if (source != null) {
        source.setGeoJson(featureCollection);
      }
    }
  }

  private static class LoadGeoJson extends AsyncTask<Void, Void, FeatureCollection> {

    private WeakReference<VariableLabelPlacementActivity> weakReference;
    private Style style;

    LoadGeoJson(Style style, VariableLabelPlacementActivity activity) {
      this.style = style;
      this.weakReference = new WeakReference<>(activity);
    }

    @Override
    protected FeatureCollection doInBackground(Void... voids) {
      try {
        VariableLabelPlacementActivity activity = weakReference.get();
        if (activity != null) {
          InputStream inputStream = activity.getAssets().open("poi_places.geojson");
          return FeatureCollection.fromJson(convertStreamToString(inputStream));
        }
      } catch (Exception exception) {
        Timber.e("Exception loading GeoJSON: %s", exception.toString());
      }
      return null;
    }

    static String convertStreamToString(InputStream is) {
      Scanner scanner = new Scanner(is).useDelimiter("\\A");
      return scanner.hasNext() ? scanner.next() : "";
    }

    @Override
    protected void onPostExecute(@Nullable FeatureCollection featureCollection) {
      super.onPostExecute(featureCollection);
      VariableLabelPlacementActivity activity = weakReference.get();
      if (activity != null && featureCollection != null) {
        activity.onDataLoaded(style, featureCollection);
      }
    }
  }

  // Add the mapView lifecycle to the activity's lifecycle methods
  @Override
  public void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override
  protected void onStart() {
    super.onStart();
    mapView.onStart();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mapView.onStop();
  }

  @Override
  public void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }
}
