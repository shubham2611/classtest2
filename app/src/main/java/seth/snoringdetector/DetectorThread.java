package seth.snoringdetector;

/**
 * Created by guptal on 10-Oct-17.
 */

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Handler;
import android.os.Message;

import com.musicg.wave.WaveHeader;




public class DetectorThread extends Thread {

    private RecorderThread recorder;
    private volatile Thread _thread;
    private WaveHeader waveHeader;
    private SnoringApi snoringApi;
    // ----------------------------------
    Handler alarmhandler;

    // ----------------------------------

    public DetectorThread(RecorderThread recorder, Handler alarmhandler) {
        // TODO Auto-generated constructor stub
        this.recorder = recorder;
        AudioRecord audioRecord = recorder.getAudioRecord();

        this.alarmhandler = alarmhandler;

        int bitsPerSample = 0;
        if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) {
            bitsPerSample = 16;
        } else if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_8BIT) {
            bitsPerSample = 8;
        }

        int channel = 0;
        // whistle detection only supports mono channel
        if (audioRecord.getChannelConfiguration() == AudioFormat.CHANNEL_CONFIGURATION_MONO) {
            channel = 1;
        }

        // TODO: added detection init
        waveHeader = new WaveHeader();
        waveHeader.setChannels(channel);
        waveHeader.setBitsPerSample(bitsPerSample);
        waveHeader.setSampleRate(audioRecord.getSampleRate());
        snoringApi = new SnoringApi(waveHeader);
    }

    public void stopDetection() {
        // TODO Auto-generated method stub
        _thread = null;
    }

    public int getTotalSnoreDetected() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void start() {
        _thread = new Thread(this);
        _thread.start();
    }

    public void run() {
        // @TODO: added run method content
        try {
            byte[] buffer;
            // initBuffer();

            Thread thisThread = Thread.currentThread();
            while (_thread == thisThread) {
                // detect sound
                buffer = recorder.getFrameBytes();

                // audio analyst
                if (buffer != null) {
                    System.out.println("How many bytes? " + buffer.length);
                    AlarmStaticVariable.snoringCount = snoringApi
                            .isSnoring(buffer);
                    System.out.println("count="
                            + AlarmStaticVariable.snoringCount);
                    if (AlarmStaticVariable.snoringCount >= AlarmStaticVariable.sampleCount) {
                        AlarmStaticVariable.snoringCount = 0;
                        if (!AlarmStaticVariable.inProcess) {
                            AlarmStaticVariable.inProcess = true;
                            int level = 1;
                            Message msg = new Message();
                            msg.arg1 = level;
                            alarmhandler.sendMessage(msg);
                        }

                    }

                    // end snore detection

                } else {
                    // no sound detected
                    MainActivity.snoreValue = 0;
                }
                // end audio analyst
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

