/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.classification;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.tensorflow.lite.examples.classification.env.ImageUtils;
import org.tensorflow.lite.examples.classification.env.Logger;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Device;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Recognition;

import java.nio.ByteBuffer;
import java.util.List;

public abstract class CameraActivity extends AppCompatActivity
    implements OnImageAvailableListener,
        Camera.PreviewCallback,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener {
  private static final Logger LOGGER = new Logger();

  private static final int PERMISSIONS_REQUEST = 1;

  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  protected int previewWidth = 0;
  protected int previewHeight = 0;
  private Handler handler;
  private HandlerThread handlerThread;
  private boolean useCamera2API;
  private boolean isProcessingFrame = false;
  private byte[][] yuvBytes = new byte[3][];
  private int[] rgbBytes = null;
  private int yRowStride;
  private Runnable postInferenceCallback;
  private Runnable imageConverter;
  private FrameLayout bottomSheetLayout;
  private LinearLayout gestureLayout, bottomSheetBackground;
  private BottomSheetBehavior<LinearLayout> sheetBehavior;
  protected TextView recognitionTextView,
      recognition1TextView,
      recognition2TextView,
      recognitionInfoTextView,
      recognitionValueTextView,
      recognition1ValueTextView,
      recognition2ValueTextView;
  protected TextView frameValueTextView,
      cropValueTextView,
      cameraResolutionTextView,
      rotationTextView,
      inferenceTimeTextView;
  protected ImageView bottomSheetArrowImageView;
  private ImageView plusImageView, minusImageView, recognitionImageView;
  private Spinner deviceSpinner;
  private TextView threadsTextView;

  private Device device = Device.CPU;
  private int numThreads = -1;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    LOGGER.d("onCreate " + this);
    super.onCreate(null);
    // 화면을 켜진 상태로 유지
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    setContentView(R.layout.tfe_ic_activity_camera);

    if (hasPermission()) {
      setFragment();
    } else {
      requestPermission();
    }

    // bottom_sheet에 있는 element의 id를 가져온다.
    /*
    threadsTextView = findViewById(R.id.threads);
    // plus, minus : Thread에서 개수를 세는 element
    plusImageView = findViewById(R.id.plus);
    minusImageView = findViewById(R.id.minus);
     */
    /*
    deviceSpinner = findViewById(R.id.device_spinner);
    */
    bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
    bottomSheetBackground = findViewById(R.id.detected_label);
    // gestureLayout : bottom_sheet 에서 사물인식 결과의 텍스트가 나오는 layout
    gestureLayout = findViewById(R.id.gesture_layout);
    /*
    sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
     */
    /*
    bottomSheetArrowImageView = findViewById(R.id.bottom_sheet_arrow);
     */

    ViewTreeObserver vto = gestureLayout.getViewTreeObserver();
    // addOnGlobalLayoutListener() : gestureLayout이 다 그려졌는지 확인하는 함수
    vto.addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
              gestureLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
              gestureLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            //                int width = bottomSheetLayout.getMeasuredWidth();

            /* bottom_sheet의 높이로 설정해주었다.
            int height = bottomSheetLayout.getMeasuredHeight();
            */
            int height = gestureLayout.getMeasuredHeight();

            /* setPeekHeight() : bottom_sheet 가 접힐 때 bottom_sheet의 높이를 설정한다.
            sheetBehavior.setPeekHeight(height);
            */
          }
        });

    /* setHideable() : bottom_sheet를 스왑했을 때 완전히 숨길 수 있는지 여부에 관한 함수
    sheetBehavior.setHideable(false);
    */

    // bottom_sheet를 open 하거나 close 하는 버튼에 대한 내용
    /*
    sheetBehavior.setBottomSheetCallback( // setBottomSheetCallback() : bottom_sheet 이벤트가 발생할 때 알려주기 위한 콜백. (사용 비추천)
        new BottomSheetBehavior.BottomSheetCallback() {
          @Override
          public void onStateChanged(@NonNull View bottomSheet, int newState) {
            switch (newState) {
              case BottomSheetBehavior.STATE_HIDDEN:
                break;
              case BottomSheetBehavior.STATE_EXPANDED:
                {
                  bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_down);
                }
                break;
              case BottomSheetBehavior.STATE_COLLAPSED:
                {
                  bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
                }
                break;
              case BottomSheetBehavior.STATE_DRAGGING:
                break;
              case BottomSheetBehavior.STATE_SETTLING:
                bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
                break;
            }
          }

          @Override
          public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });
      */

    // bottom_sheet의 element의 id를 가져오는 부분

    // 사물 인식된 결과를 출력하는 element들
    // 순서대로 클래스 명과 퍼센트 (ex. plastic 18%)
    recognitionTextView = findViewById(R.id.detected_item);
    recognitionInfoTextView = findViewById(R.id.detected_item_info);
//    recognitionValueTextView = findViewById(R.id.detected_item_value);
    recognitionImageView = findViewById(R.id.detected_item_img);
//    recognition1TextView = findViewById(R.id.detected_item1);
//    recognition1ValueTextView = findViewById(R.id.detected_item1_value);
//    recognition2TextView = findViewById(R.id.detected_item2);
//    recognition2ValueTextView = findViewById(R.id.detected_item2_value);

    // 사물 인식 metaData
    /*
    frameValueTextView = findViewById(R.id.frame_info);
    cropValueTextView = findViewById(R.id.crop_info);
    cameraResolutionTextView = findViewById(R.id.view_info);
    rotationTextView = findViewById(R.id.rotation_info);
    inferenceTimeTextView = findViewById(R.id.inference_info);
     */

    /*
    deviceSpinner.setOnItemSelectedListener(this);
    */

    /*
    plusImageView.setOnClickListener(this);
    minusImageView.setOnClickListener(this);
     */

    // device : bottom_sheet 안에 있는 device의 값을 string 형으로 반환하고, 그 값으로 초기화한다.
    device = Device.valueOf("CPU");
    /*
    device = Device.valueOf(deviceSpinner.getSelectedItem().toString());
     */
    // numThreads : bottom_sheet 안에 있는 threads의 값을 integer 형으로 변경하고, 그 값으로 초기화한다.
    numThreads = Integer.parseInt("1");
    /*
    numThreads = Integer.parseInt(threadsTextView.getText().toString().trim());
     */
  }

  protected int[] getRgbBytes() {
    imageConverter.run();
    return rgbBytes;
  }

  protected int getLuminanceStride() {
    return yRowStride;
  }

  protected byte[] getLuminance() {
    return yuvBytes[0];
  }

  /** Callback for android.hardware.Camera API */
  @Override
  public void onPreviewFrame(final byte[] bytes, final Camera camera) {
    if (isProcessingFrame) {
      LOGGER.w("Dropping frame!");
      return;
    }

    try {
      // Initialize the storage bitmaps once when the resolution is known.
      if (rgbBytes == null) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        previewHeight = previewSize.height;
        previewWidth = previewSize.width;
        rgbBytes = new int[previewWidth * previewHeight];
        onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
      }
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      return;
    }

    isProcessingFrame = true;
    yuvBytes[0] = bytes;
    yRowStride = previewWidth;

    imageConverter =
        new Runnable() {
          @Override
          public void run() {
            ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
          }
        };

    postInferenceCallback =
        new Runnable() {
          @Override
          public void run() {
            camera.addCallbackBuffer(bytes);
            isProcessingFrame = false;
          }
        };
    processImage();
  }

  /** Callback for Camera2 API */
  @Override
  public void onImageAvailable(final ImageReader reader) {
    // We need wait until we have some size from onPreviewSizeChosen
    if (previewWidth == 0 || previewHeight == 0) {
      return;
    }
    if (rgbBytes == null) {
      rgbBytes = new int[previewWidth * previewHeight];
    }
    try {
      final Image image = reader.acquireLatestImage();

      if (image == null) {
        return;
      }

      if (isProcessingFrame) {
        image.close();
        return;
      }
      isProcessingFrame = true;
      Trace.beginSection("imageAvailable");
      final Plane[] planes = image.getPlanes();
      fillBytes(planes, yuvBytes);
      yRowStride = planes[0].getRowStride();
      final int uvRowStride = planes[1].getRowStride();
      final int uvPixelStride = planes[1].getPixelStride();

      imageConverter =
          new Runnable() {
            @Override
            public void run() {
              ImageUtils.convertYUV420ToARGB8888(
                  yuvBytes[0],
                  yuvBytes[1],
                  yuvBytes[2],
                  previewWidth,
                  previewHeight,
                  yRowStride,
                  uvRowStride,
                  uvPixelStride,
                  rgbBytes);
            }
          };

      postInferenceCallback =
          new Runnable() {
            @Override
            public void run() {
              image.close();
              isProcessingFrame = false;
            }
          };

      processImage();
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      Trace.endSection();
      return;
    }
    Trace.endSection();
  }

  @Override
  public synchronized void onStart() {  // synchronized는 함수에 포함된 해당 객체에 lock을 건다.
    LOGGER.d("onStart " + this);
    super.onStart();
  }

  @Override
  public synchronized void onResume() {
    LOGGER.d("onResume " + this);
    super.onResume();

    handlerThread = new HandlerThread("inference");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
  }

  @Override
  public synchronized void onPause() {
    LOGGER.d("onPause " + this);

    handlerThread.quitSafely();
    try {
      handlerThread.join();
      handlerThread = null;
      handler = null;
    } catch (final InterruptedException e) {
      LOGGER.e(e, "Exception!");
    }

    super.onPause();
  }

  @Override
  public synchronized void onStop() {
    LOGGER.d("onStop " + this);
    super.onStop();
  }

  @Override
  public synchronized void onDestroy() {
    LOGGER.d("onDestroy " + this);
    super.onDestroy();
  }

  protected synchronized void runInBackground(final Runnable r) {
    if (handler != null) {
      handler.post(r);
    }
  }

  @Override
  public void onRequestPermissionsResult(
      final int requestCode, final String[] permissions, final int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIONS_REQUEST) {
      if (allPermissionsGranted(grantResults)) {
        setFragment();
      } else {
        requestPermission();
      }
    }
  }

  private static boolean allPermissionsGranted(final int[] grantResults) {
    for (int result : grantResults) {
      if (result != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }

  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
        Toast.makeText(
                CameraActivity.this,
                "Camera permission is required for this demo",
                Toast.LENGTH_LONG)
            .show();
      }
      requestPermissions(new String[] {PERMISSION_CAMERA}, PERMISSIONS_REQUEST);
    }
  }

  // Returns true if the device supports the required hardware level, or better.
  private boolean isHardwareLevelSupported(
      CameraCharacteristics characteristics, int requiredLevel) {
    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
      return requiredLevel == deviceLevel;
    }
    // deviceLevel is not LEGACY, can use numerical sort
    return requiredLevel <= deviceLevel;
  }

  // 카메라 세팅
  private String chooseCamera() {
    final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    try {
      for (final String cameraId : manager.getCameraIdList()) {
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

        // We don't use a front facing camera in this sample.
        final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
          continue;
        }

        final StreamConfigurationMap map =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        if (map == null) {
          continue;
        }

        // Fallback to camera1 API for internal cameras that don't have full support.
        // This should help with legacy situations where using the camera2 API causes
        // distorted or otherwise broken previews.
        useCamera2API =
            (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                || isHardwareLevelSupported(
                    characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
        LOGGER.i("Camera API lv2?: %s", useCamera2API);
        return cameraId;
      }
    } catch (CameraAccessException e) {
      LOGGER.e(e, "Not allowed to access camera");
    }

    return null;
  }

  protected void setFragment() {  // fragment는 액티비티를 모듈화 한 단위. 여러 개의 fragment로 액티비티에 여러 개의 UI를 빌드할 수 있다.
    String cameraId = chooseCamera();

    Fragment fragment;
    if (useCamera2API) {
      CameraConnectionFragment camera2Fragment =
          CameraConnectionFragment.newInstance(
              new CameraConnectionFragment.ConnectionCallback() {
                @Override
                public void onPreviewSizeChosen(final Size size, final int rotation) {
                  previewHeight = size.getHeight();
                  previewWidth = size.getWidth();
                  CameraActivity.this.onPreviewSizeChosen(size, rotation);
                }
              },
              this,
              getLayoutId(),
              getDesiredPreviewFrameSize());

      camera2Fragment.setCamera(cameraId);
      fragment = camera2Fragment;
    } else {
      fragment =
          new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
    }

    getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
  }

  protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes) { // plane : 이미지 데이터의 단일 색상 면(?), 이미지가 종료되면 여기 접근할 수 없다.
    // Because of the variable row stride it's not possible to know in
    // advance the actual necessary dimensions of the yuv planes.
    for (int i = 0; i < planes.length; ++i) {
      final ByteBuffer buffer = planes[i].getBuffer();
      if (yuvBytes[i] == null) {
        LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
        yuvBytes[i] = new byte[buffer.capacity()];
      }
      buffer.get(yuvBytes[i]);
    }
  }

  protected void readyForNextImage() {
    if (postInferenceCallback != null) {
      postInferenceCallback.run();
    }
  }

  protected int getScreenOrientation() {
    switch (getWindowManager().getDefaultDisplay().getRotation()) {
      case Surface.ROTATION_270:
        return 270;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_90:
        return 90;
      default:
        return 0;
    }
  }

  //
  //
  //

  @UiThread
  // bottom_sheet에 있는 element에게 출력할 데이터를 세팅하는 함수 정의

  // 사물인식 결과 데이터를 출력
  protected void showResultsInBottomSheet(List<Recognition> results) {
    // 물체를 분류한 후 카메라 화면 띄우기
    if (results != null && results.size() >= 1) {
      // 1. 분류한 데이터값을 실시간으로 가져오기
      Recognition recognition = results.get(0);
      if (recognition != null) {
        // 2. 값이 40프로 이상 되면 bottom_sheet visible
        if (recognition.getConfidence() != null) {
          if (recognition.getConfidence() > 0.55) {
            bottomSheetLayout.setVisibility(View.VISIBLE);
            if (recognition.getTitle() != null) {
              //recognitionTextView.setText(recognition.getTitle());

              // 분류명 속성에 bold 추가
              recognitionTextView.setTypeface(null, Typeface.BOLD);

              GradientDrawable drawable = (GradientDrawable) bottomSheetBackground.getBackground();

              switch (recognition.getTitle()) {
                case "plastic" :
                  recognitionImageView.setImageResource(R.drawable.img_plastic);
                  recognitionTextView.setText("플라스틱류");
                  recognitionInfoTextView.setText("내용물을 비우고 다른 재질로 된 부분(부착상표 등)을 제거한 후 배출합니다");
                  drawable.setColor(ContextCompat.getColor(this, R.color.colorBlue));
                  break;
                case "metal" :
                  recognitionImageView.setImageResource(R.drawable.img_metal);
                  recognitionTextView.setText("캔류");
                  recognitionInfoTextView.setText("내용물을 비우고 겉 또는 속의 플라스틱 뚜껑 등은 제거한 후 배출합니다");
                  drawable.setColor(ContextCompat.getColor(this, R.color.colorGray));
                  break;
                case "glass" :
                  recognitionImageView.setImageResource(R.drawable.img_glass);
                  recognitionTextView.setText("유리류");
                  recognitionInfoTextView.setText("병뚜껑을 제거한 후 내용물을 비우고 배출합니다");
                  drawable.setColor(ContextCompat.getColor(this, R.color.colorOrange));
                  break;
                case "paper" :
                  recognitionImageView.setImageResource(R.drawable.img_paper);
                  recognitionTextView.setText("종이류");
                  recognitionInfoTextView.setText("물기에 젖지 않도록 하고 펴서 차곡차곡 쌓은 후 묶어서 배출합니다");
                  drawable.setColor(ContextCompat.getColor(this, R.color.colorGreen));
                  break;
                case "clothes" :
                  recognitionImageView.setImageResource(R.drawable.img_clothes);
                  recognitionTextView.setText("의류");
                  recognitionInfoTextView.setText("의류수거함에 배출합니다");
                  drawable.setColor(ContextCompat.getColor(this, R.color.colorPink));
                  break;
                case "shoes" :
                  recognitionImageView.setImageResource(R.drawable.img_clothes);
                  recognitionTextView.setText("의류");
                  recognitionInfoTextView.setText("의류수거함에 배출합니다");
                  drawable.setColor(ContextCompat.getColor(this, R.color.colorPink));
                  break;
                case "battery" :
                  recognitionImageView.setImageResource(R.drawable.img_battery);
                  recognitionTextView.setText("폐건전지");
                  recognitionInfoTextView.setText("동 행정복지센터 및 아파트 내 전용수거함에 배출합니다");
                  drawable.setColor(ContextCompat.getColor(this, R.color.colorPurple));
                  break;
                default:
                  recognitionImageView.setImageResource(R.drawable.img_trash);
                  recognitionTextView.setText("일반쓰레기");
                  recognitionInfoTextView.setText("일방 종량제 봉투에 담아서 배출합니다");
                  drawable.setColor(ContextCompat.getColor(this, R.color.colorBrown));
                  break;
              }

              // 레이아웃 클릭했을 때 이벤트 발생
              bottomSheetLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Intent intent = new Intent(view.getContext(), DetailActivity.class);
                  intent.putExtra("title", recognitionTextView.getText());
                  view.getContext().startActivity(intent);
                }
              });
            }
            /* 사물 인식 퍼센트 출력
            recognitionValueTextView.setText(
                    String.format("%.2f", (100 * recognition.getConfidence())) + "%");
             */


          } else { // 3. 아니면 invisible
            bottomSheetLayout.setVisibility(View.INVISIBLE);
          }
        }
      }
    }
    /*
    // 기존 코드 삭제
    if (results != null && results.size() >= 3) {
      Recognition recognition = results.get(0);
      if (recognition != null) {
        if (recognition.getTitle() != null) recognitionTextView.setText(recognition.getTitle());
        if (recognition.getConfidence() != null)
          recognitionValueTextView.setText(
              String.format("%.2f", (100 * recognition.getConfidence())) + "%");
      }

      Recognition recognition1 = results.get(1);
      if (recognition1 != null) {
        if (recognition1.getTitle() != null) recognition1TextView.setText(recognition1.getTitle());
        if (recognition1.getConfidence() != null)
          recognition1ValueTextView.setText(
              String.format("%.2f", (100 * recognition1.getConfidence())) + "%");
      }

      Recognition recognition2 = results.get(2);
      if (recognition2 != null) {
        if (recognition2.getTitle() != null) recognition2TextView.setText(recognition2.getTitle());
        if (recognition2.getConfidence() != null)
          recognition2ValueTextView.setText(
              String.format("%.2f", (100 * recognition2.getConfidence())) + "%");
      }
    }

     */
  }

  // 사물인식 metaData
  protected void showFrameInfo(String frameInfo) {
    frameValueTextView.setText(frameInfo);
  }

  protected void showCropInfo(String cropInfo) {
    cropValueTextView.setText(cropInfo);
  }

  protected void showCameraResolution(String cameraInfo) {
    cameraResolutionTextView.setText(cameraInfo);
  }

  protected void showRotationInfo(String rotation) {
    rotationTextView.setText(rotation);
  }

  protected void showInference(String inferenceTime) {
    inferenceTimeTextView.setText(inferenceTime);
  }

  // 아래 두 UI는 값을 다른 곳에서 받아서(get) 출력한다(set)

  // bottom_sheet 안에 있는 device 의 값을 가져오는 함수
  protected Device getDevice() {
    return device;
  }

  private void setDevice(Device device) {
    if (this.device != device) {
      LOGGER.d("Updating  device: " + device);
      this.device = device;
      final boolean threadsEnabled = device == Device.CPU;
      plusImageView.setEnabled(threadsEnabled);
      minusImageView.setEnabled(threadsEnabled);
      threadsTextView.setText(threadsEnabled ? String.valueOf(numThreads) : "N/A");
      onInferenceConfigurationChanged();
    }
  }

  // integer 형으로 변환한 bottom_sheet 안에 있는 Threads의 값을 가져오는 함수
  protected int getNumThreads() {
    return numThreads;
  }

  private void setNumThreads(int numThreads) {
    if (this.numThreads != numThreads) {
      LOGGER.d("Updating  numThreads: " + numThreads);
      this.numThreads = numThreads;
      onInferenceConfigurationChanged();
    }
  }

  protected abstract void processImage();

  protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

  protected abstract int getLayoutId();

  protected abstract Size getDesiredPreviewFrameSize();

  protected abstract void onInferenceConfigurationChanged();

  @Override
  public void onClick(View v) {
    /*
    CameraConnection~.java 에서 onClick()을 사용하지 않아서 주석 처리함. (없어도 실행됨)

    if (v.getId() == R.id.plus) {
      String threads = threadsTextView.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      if (numThreads >= 9) return;
      setNumThreads(++numThreads);
      threadsTextView.setText(String.valueOf(numThreads));
    } else if (v.getId() == R.id.minus) {
      String threads = threadsTextView.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      if (numThreads == 1) {
        return;
      }
      setNumThreads(--numThreads)
      threadsTextView.setText(String.valueOf(numThreads));
    }
    */
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    /*
    if (parent == deviceSpinner) {
      setDevice(Device.valueOf(parent.getItemAtPosition(pos).toString()));
    }
    */
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }
}
