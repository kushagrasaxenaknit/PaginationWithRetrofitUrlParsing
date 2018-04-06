package kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement;

import android.util.Log;
import android.view.Surface;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

/**
 * Created by Kushagra Saxena on 06/04/2018.
 */

public class ComponentListener extends Player.DefaultEventListener implements
        VideoRendererEventListener, AudioRendererEventListener {

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        String stateString;
        switch (playbackState) {
            case Player.STATE_IDLE:
                stateString = "ExoPlayer.STATE_IDLE      -";
                break;
            case Player.STATE_BUFFERING:
                stateString = "ExoPlayer.STATE_BUFFERING -";
                break;
            case Player.STATE_READY:
                stateString = "ExoPlayer.STATE_READY     -";
                break;
            case Player.STATE_ENDED:
                stateString = "ExoPlayer.STATE_ENDED     -";
                break;
            default:
                stateString = "UNKNOWN_STATE             -";
                break;
        }
        Log.d("Song Listener", "changed state to " + stateString + " playWhenReady: " + playWhenReady);
    }

    // Implementing VideoRendererEventListener.

    @Override
    public void onVideoEnabled(DecoderCounters counters) {
        // Do nothing.
    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
        // Do nothing.
    }

    @Override
    public void onVideoInputFormatChanged(Format format) {
        // Do nothing.
    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {
        // Do nothing.
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        // Do nothing.
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {
        // Do nothing.
    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {
        // Do nothing.
    }

    // Implementing AudioRendererEventListener.

    @Override
    public void onAudioEnabled(DecoderCounters counters) {
        // Do nothing.
    }

    @Override
    public void onAudioSessionId(int audioSessionId) {
        // Do nothing.
    }

    @Override
    public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
        // Do nothing.
    }

    @Override
    public void onAudioInputFormatChanged(Format format) {
        // Do nothing.
    }

    @Override
    public void onAudioSinkUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        // Do nothing.
    }

    @Override
    public void onAudioDisabled(DecoderCounters counters) {
        // Do nothing.
    }

}