package media.player.utils;
import media.player.fragments.AudioFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HeadsetPlugReceiver extends BroadcastReceiver {

	/* Variables */
	private AudioFragment audioFragment;
	private HeadphoneState previousState = HeadphoneState.NULL;
	private boolean isAudioFragmentPaused = false;
	/* End of variables */
	
	// State of Headphone
	public enum HeadphoneState{
		NULL, PLUGGED, UNPLUGGED;
	}
	
	// Constructor
	public HeadsetPlugReceiver(AudioFragment af)
	{
		this.audioFragment = af;
	}
	
	// Receive a broadcast
	@Override
	public void onReceive(Context context, Intent intent) {
		
		// Is the action the state of the headpones? Quit otherwise!
		if (!intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
			return;
		}
		
		// Get the state of the headphones
		HeadphoneState connectedHeadphones = HeadphoneState.UNPLUGGED;
		if(intent.getIntExtra("state", 0) == 1)
			connectedHeadphones = HeadphoneState.PLUGGED;	
		//String headsetName = intent.getStringExtra("name"); // Not used
		
		// If the state go to UNPLUGGED, when it was PLUGGED => music is paused
		if(previousState.equals(HeadphoneState.PLUGGED) && connectedHeadphones.equals(HeadphoneState.UNPLUGGED))
		{
			audioFragment.onPause();
			isAudioFragmentPaused = true;
		}
		else
		{
			if(isAudioFragmentPaused)
			{
				audioFragment.onResume(); // music is resumed
				isAudioFragmentPaused = false;
			}
		}
		previousState = connectedHeadphones;
		//Log.w("simon","test headphones, connected: "+connectedHeadphones+", connected mic: "+connectedMicrophone+", name: "+headsetName);
	}
}