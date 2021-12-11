package com.example.torchbearer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class PathMapView extends MapView implements OnMapReadyCallback {

    private static final float LINE_WIDTH_IN_METERS = 45;
    private OnMapReadyCallback mMapReadyCallback;
    private GoogleMap mGoogleMap;
    private Paint mPaintPath;
    private Paint mPaintBackground;
    private Paint mPaintBitmap;
    private List<List<LatLng>> mPathPoints;
    private Bitmap mBitmap;
    private Canvas mBitmapCanvas;

    @SuppressLint("ValidFragment")
    public PathMapView(@NonNull Context context) {
        super(context);
        init();
    }

    public PathMapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PathMapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PathMapView(@NonNull Context context, @Nullable GoogleMapOptions options) {
        super(context, options);
        init();
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        drawPolylineOverTheMap(canvas);
//        canvas.restore();
    }

    private void drawPolylineOverTheMap(Canvas canvas) {
        if (mGoogleMap == null || mPathPoints == null) {
            return;
        }

        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            mBitmapCanvas = new Canvas(mBitmap);
        }

        mBitmapCanvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaintBackground);

        double metersPerPixel = (Math.cos(mGoogleMap.getCameraPosition().target.latitude * Math.PI / 180) * 2 * Math.PI * 6378137) / (256 * Math.pow(2, mGoogleMap.getCameraPosition().zoom));
        float lineWidth = (float) (LINE_WIDTH_IN_METERS / metersPerPixel);
        mPaintPath.setStrokeWidth(lineWidth);

        Projection projection = mGoogleMap.getProjection();
        for (List<LatLng> line : mPathPoints) {
            for (int i = 1; i < line.size(); i++) {
                final Point point1 = projection.toScreenLocation(line.get(i-1));
                final Point point2 = projection.toScreenLocation(line.get(i));
                mBitmapCanvas.drawLine(point1.x, point1.y, point2.x, point2.y, mPaintPath);
            }
        }

        canvas.drawBitmap(mBitmap, null, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), mPaintBitmap);
    }

    private void init() {
        setWillNotDraw(false);

        mPaintPath = new Paint();
        mPaintPath.setColor(Color.WHITE);
        mPaintPath.setStrokeWidth(25);
        mPaintPath.setAlpha(255);
        mPaintPath.setStrokeCap(Paint.Cap.ROUND);

        mPaintBackground = new Paint();
        mPaintBackground.setColor(Color.BLACK);
        mPaintBackground.setAlpha(255);
        mPaintBackground.setStrokeWidth(15);

        mPaintBitmap = new Paint();
        mPaintBitmap.setAlpha(50);

        mPathPoints = new ArrayList<>();
    }

    @Override
    public void getMapAsync(OnMapReadyCallback callback) {
        mMapReadyCallback = callback;
        super.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                invalidate();
            }
        });
        if (mMapReadyCallback != null) {
            mMapReadyCallback.onMapReady(googleMap);
        }
    }

    public void setPathPoints(final List<List<LatLng>> pathPoints) {
        mPathPoints = pathPoints;
        this.mPathPoints.add(new ArrayList<>());
    }

    public void addPoint(final LatLng point) {
        if (this.mPathPoints.isEmpty()) {
            this.mPathPoints.add(new ArrayList<>());
        }
        this.mPathPoints.get(this.mPathPoints.size() - 1).add(point);
    }

    public void resetLine() {
        this.mPathPoints.add(new ArrayList<>());
    }

    public List<List<LatLng>> getmPathPoints() {
        return mPathPoints;
    }
}
