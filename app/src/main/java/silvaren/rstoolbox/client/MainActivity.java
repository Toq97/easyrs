package silvaren.rstoolbox.client;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.google.common.base.Optional;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.script_spinner)
    AppCompatSpinner scriptSpinner;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.script_flavor_spinner)
    AppCompatSpinner flavorSpinner;

    @BindView(R.id.image_format_seek)
    SeekBar imageFormatSeekBar;

    private Optional<Bitmap> sampleBitmap = Optional.absent();

    private SeekBar.OnSeekBarChangeListener onImageFormatSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            processImageWithSelectedOptions();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tools_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scriptSpinner.setAdapter(adapter);

        imageFormatSeekBar.setOnSeekBarChangeListener(onImageFormatSeekBarListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sampleBitmap = Optional.of(loadBitmap());

        String selectedItem = (String) scriptSpinner.getSelectedItem();
        processImage(selectedItem, ImageProcesses.ImageFormat.BITMAP);
    }

    @OnItemSelected(R.id.script_spinner)
    public void scriptSpinnerListen() {
        processImageWithSelectedOptions();
    }

    @OnItemSelected(R.id.script_flavor_spinner)
    public void flavorSpinnerListen() {
        processImageWithSelectedOptions();
    }

    private void processImageWithSelectedOptions() {
        String selectedItem = (String) scriptSpinner.getSelectedItem();
        updateFlavor(selectedItem);
        processImage(selectedItem,
                ImageProcesses.ImageFormat.valueOf(imageFormatSeekBar.getProgress()));
    }

    private void updateFlavor(String selectedItem) {
        Map<String, Integer> flavorMap = ImageProcesses.flavorMap(this);
        Optional<Integer> stringArrayId = Optional.fromNullable(flavorMap.get(selectedItem));
        if (stringArrayId.isPresent()) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    stringArrayId.get(), android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            flavorSpinner.setAdapter(adapter);
            flavorSpinner.setVisibility(View.VISIBLE);
        } else {
            flavorSpinner.setVisibility(View.GONE);
        }
    }

    private Bitmap loadBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        options.inDither = false;
        options.inPurgeable = true;
        return BitmapFactory.decodeResource(getResources(), R.drawable.sample, options);
    }

    private void processImage(String selectedItem, final ImageProcesses.ImageFormat imageFormat) {
        if (!sampleBitmap.isPresent())
            sampleBitmap = Optional.of(loadBitmap());

        final Optional<ImageProcesses.ImageProcess> imageProcess = Optional.fromNullable(
                ImageProcesses.processMap(this).get(selectedItem));
        if (imageProcess.isPresent()) {
            final ProgressDialog progressDialog = ProgressDialog.show(this, "Loading",
                    "Wait while loading...");
            new AsyncTask<Void, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Void... params) {
                    Bitmap processedBitmap = imageProcess.get().processImage(MainActivity.this,
                            sampleBitmap.get(), imageFormat);
                    return processedBitmap;
                }

                @Override
                protected void onPostExecute(Bitmap processedBitmap) {
                    imageView.setImageBitmap(processedBitmap);
                    progressDialog.dismiss();
                }
            }.execute();
        }
    }
}
