package seth.snoringdetector;

/**
 * Created by guptal on 10-Oct-17.
 */



import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

public class RecorderThread extends Thread {
    private AudioRecord audioRecord;
    private boolean isRecording;


    private int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int sampleRate = 44100;
    private int frameByteSize = 1024; // for 1024 fft size (16bit sample size)

    byte[] buffer;
    byte[] totalBuf;
    int cnt;

    // showVariableThread showVariable;
    Handler showhandler;

    public RecorderThread(Handler showhandler) {
        this.showhandler = showhandler;
        int recBufSize = AudioRecord.getMinBufferSize(sampleRate,
                channelConfiguration, audioEncoding); // need to be larger than
        // size of a frame
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, channelConfiguration, audioEncoding, recBufSize);

        buffer = new byte[frameByteSize];
        totalBuf = new byte[AlarmStaticVariable.sampleSize * 2];
        cnt = 0;


    }

    public AudioRecord getAudioRecord() {
        return audioRecord;
    }

    public boolean isRecording() {
        return this.isAlive() && isRecording;
    }

    public void startRecording() {
        try {
            audioRecord.startRecording();
            isRecording = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        try {
            audioRecord.stop();
            isRecording = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getFrameBytes() {

        int bufferReadResult = audioRecord.read(buffer, 0, frameByteSize);

        // analyze sound
        int totalAbsValue = 0;
        short sample = 0;
        short[] tmp = new short[frameByteSize];
        // float averageAbsValue = 0.0f;
        AlarmStaticVariable.absValue = 0.0f;

        for (int i = 0; i < frameByteSize; i += 2) {
            sample = (short) ((buffer[i]) | buffer[i + 1] << 8);
            tmp[i] = sample;
            totalAbsValue += Math.abs(sample);
        }
        AlarmStaticVariable.absValue = totalAbsValue / frameByteSize / 2;

        Message msg = new Message();
        msg.obj = AlarmStaticVariable.absValue;
        showhandler.sendMessage(msg);

        for (int i = 0; i < buffer.length; i++) {
            totalBuf[cnt++] = buffer[i];
        }

        // ----------save into buf----------------------
        short[] tmpBuf = new short[bufferReadResult
                / AlarmStaticVariable.rateX];
        for (int i = 0, ii = 0; i < tmpBuf.length; i++, ii = i
                * AlarmStaticVariable.rateX) {
            tmpBuf[i] = tmp[ii];
        }
        synchronized (AlarmStaticVariable.inBuf) {//
            AlarmStaticVariable.inBuf.add(tmpBuf);// add data
        }
        // ----------save into buf----------------------

        // System.out.println(cnt + " vs " + AlarmStaticVariables.sampleSize);
        if (cnt > AlarmStaticVariable.sampleSize) {
            cnt = 0;
            return totalBuf;
        } else
            return null;
        // return buffer;
    }

    public void run() {
        startRecording();
    }

}
