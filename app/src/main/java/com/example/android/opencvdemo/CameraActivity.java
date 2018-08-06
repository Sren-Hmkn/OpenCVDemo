

package com.example.android.opencvdemo;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener {

    public static final String TESS_DATA = "/tessdata";
    private static final String TAG = "CameraActivity";

    static {
        if (!(OpenCVLoader.initDebug())) {
            Log.d(TAG, "  OpenCVLoader.initDebug(), working.");
        } else {
            Log.d(TAG, "  OpenCVLoader.initDebug(), not working.");
        }
    }

    JavaCameraView javaCameraView;
    Button makeNormal, makeGray, makeCanny, makeDilate, makeErode;
    Button makeRead;
    TextView readTextView;
    Bitmap bitmap;
    Mat usedMat;
    MyTessOCR mTessOCR;

    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    javaCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }

        //Disable OpenCVManager installation
        @Override
        public void onPackageInstall(final int operation, final InstallCallbackInterface callback) {
            switch (operation) {
                case InstallCallbackInterface.NEW_INSTALLATION: {
                    Log.i(TAG, "Tried to install OpenCV Manager package, but I still don't believe that you need it...");
                    break;
                }
                default: {
                    super.onPackageInstall(operation, callback);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        prepareTessData();
        javaCameraView = findViewById(R.id.cam_view1);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);

        mTessOCR = new MyTessOCR(this);
        readTextView = findViewById(R.id.read_text_view);


        makeNormal = findViewById(R.id.normal_mode);
        makeGray = findViewById(R.id.gray_mode);
        makeRead = findViewById(R.id.read_button);
        makeCanny = findViewById(R.id.canny_mode);
        makeDilate = findViewById(R.id.dilate_mode);
        makeErode = findViewById(R.id.erose_mode);

        makeNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCvMaker.setNormal();

            }
        });
        makeGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCvMaker.toggleGray();
            }
        });
        makeDilate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCvMaker.toggleDilate();
            }
        });
        makeErode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCvMaker.toggleErode();
            }
        });
        makeCanny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCvMaker.toggleCanny();
            }
        });
        makeRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncOcrTask task = new AsyncOcrTask();
                task.execute();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallBack)) {
            Log.e(TAG, "  OpenCVLoader.initAsync(), not working.");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d(TAG, "  OpenCVLoader.initAsync(), working.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallBack);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        usedMat = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        javaCameraView.disableView();
    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        usedMat = inputFrame;
        OpenCvMaker.doStuffWithPic(inputFrame.getNativeObjAddr(), usedMat.getNativeObjAddr());
        return usedMat;
    }

    private void prepareTessData() {
        try {
            File dir = getExternalFilesDir(TESS_DATA);
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    Toast.makeText(getApplicationContext(), "The folder " + dir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
                }
            }
            String fileList[] = getAssets().list("tessdata");

            for (String fileName : fileList) {
                String pathToDataFile = dir + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {
                    InputStream in = getAssets().open("tessdata/" + fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    byte[] buff = new byte[1024];
                    int len;
                    while ((len = in.read(buff)) > 0) {
                        out.write(buff, 0, len);
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private class AsyncOcrTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String temp = "not working";
            bitmap = Bitmap.createBitmap(usedMat.cols(), usedMat.rows(), Bitmap.Config.ARGB_4444);
            try {
                bitmap = Bitmap.createBitmap(usedMat.cols(), usedMat.rows(), Bitmap.Config.ARGB_4444);
                Utils.matToBitmap(usedMat, bitmap);

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting rotation, according to our changes in OpenCV library,
                // because we only changed the rotation in the preview, not in the actual inputStream
                Matrix mtx = new Matrix();
                mtx.postRotate(90);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);

                temp = mTessOCR.getOCRResult(bitmap);
            } catch (Exception ex) {
                Log.d("Exception", ex.getMessage());
            }
            return temp;
        }


        @Override
        protected void onPostExecute(String foundString) {
            if (foundString == null) {
                return;
            }
            readTextView.setText(foundString);
        }
    }

}
