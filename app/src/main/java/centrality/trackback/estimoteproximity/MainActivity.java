package centrality.trackback.estimoteproximity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;

import com.estimote.internal_plugins_api.scanning.BluetoothScanner;
import com.estimote.internal_plugins_api.scanning.EstimoteTelemetryFrameB;
import com.estimote.internal_plugins_api.scanning.ScanHandler;
import com.estimote.scanning_plugin.api.EstimoteBluetoothScannerFactory;
import com.estimote.scanning_plugin.api.EstimoteBluetoothScanner;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    private static EstimoteBluetoothScannerFactory factory;
    private static BluetoothScanner scanner;
    private static ScanHandler scanHandler;
    private ListView mListView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.beacon_list_view);
        mTextView = (TextView) findViewById(R.id.start_time);
    }


    void startScanning(){
        boolean check  = SystemRequirementsChecker.checkWithDefaultDialogs(this);
        Log.d("Estimote", "SystemRequirementsChecker: " + check);
        if (check)
        {
            factory = new EstimoteBluetoothScannerFactory(getApplicationContext());
            scanner = factory.getSimpleScanner();

            scanHandler = scanner.estimoteTelemetryFrameBScan()
                    .withLowLatencyPowerMode()
                    .withOnPacketFoundAction(new Function1<EstimoteTelemetryFrameB, Unit>() {
                        @Override
                        public Unit invoke(EstimoteTelemetryFrameB tlm) {
                            Log.d("Estimote", "id: " + tlm.getShortId() + ", temp: " +  tlm.getTemperatureInCelsiusDegrees());
       /* Do something with the received telemetry packet here */
                            return null;
                        }
                    })
                    .withOnScanErrorAction(new Function1<Throwable, Unit>() {
                        @Override
                        public Unit invoke(Throwable throwable) {
                            Log.e("Estimote", "onScanError", throwable);
                            return null;
                        }
                    })
                    .withTimeout(300, TimeUnit.SECONDS)
                    .start();
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
        String str = "Start: " + sdf.format(Calendar.getInstance().getTime());

        mTextView.setText(str);

        startScanning();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                    startScanning();
//            }
//        }, 10000);
    }
}
