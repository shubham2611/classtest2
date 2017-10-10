package seth.snoringdetector;

/***/



import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AlarmManagerActivity extends Activity {

    // private Button mStartBtn1, mStartBtn2, mStopBtn, mAlarmBtn, mRecordBtn;
    private EditText mTxtSeconds;
    private Toast mToast;
    //  private Handler rhandler = new Handler();

    private static double voice = 1000;// input

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AlarmStaticVariable.level = AlarmStaticVariable.level1;
        AlarmStaticVariable.partten = AlarmStaticVariable.pattern1;

    }

}
