package com.mapbox.mapboxandroiddemo.examples.styles;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxandroiddemo.R;
import com.mapbox.mapboxandroiddemo.utils.JsonLoader;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;


import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_LEFT;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_RIGHT;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_TOP;

import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_JUSTIFY_AUTO;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textJustify;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textVariableAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textRadialOffset;


public class VariableLabelPlacementActivity extends AppCompatActivity
        implements JsonLoader.DataUi<FeatureCollection> {

  private static final String MAPS_STYLE = "mapbox://styles/mapbox/light-v9";
  private static final String GEO_JSON_FILE = "poi_places.geojson";

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
        mapboxMap.setStyle(new Style.Builder()
                .fromUrl(MAPS_STYLE),
                new Style.OnStyleLoaded() {

            @Override
            public void onStyleLoaded(@NonNull final Style style) {

              VariableLabelPlacementActivity.this.mapboxMap = mapboxMap;
              new JsonLoader<FeatureCollection>(VariableLabelPlacementActivity.this)
                .execute();
            }
          });
        }
    });
  }

  @Override
  public FeatureCollection loadData() throws IOException {

    InputStream inputStream = this.getAssets().open(GEO_JSON_FILE);
    Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
    String jsonString = scanner.hasNext() ? scanner.next() : "";
    return FeatureCollection.fromJson(jsonString);
  }

  @Override
  public void onDataLoaded(FeatureCollection featureCollection) {
    GeoJsonSource geoJsonSource = new GeoJsonSource(GEOJSON_SRC_ID, featureCollection);
    if (mapboxMap != null) {
      final Style style = mapboxMap.getStyle();
      if (style != null) {
        style.addSource(geoJsonSource);

        // Adds a SymbolLayer to display POI labels
        SymbolLayer poiLabelsLayer = new SymbolLayer(POI_LABELS_LAYER_ID, GEOJSON_SRC_ID);
        poiLabelsLayer.withProperties(
                //iconImage(POI_ICON),
                textField(get("description")),
                textSize(17f),
                textColor(Color.RED),
                textVariableAnchor(
                  new String[]{TEXT_ANCHOR_TOP, TEXT_ANCHOR_BOTTOM,
                               TEXT_ANCHOR_LEFT, TEXT_ANCHOR_RIGHT}),
                textJustify(TEXT_JUSTIFY_AUTO),
                textRadialOffset(0.5f)
        );
        style.addLayer(poiLabelsLayer);
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
