package com.dryerzinia.pokemon.util.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class AudioPlayer {
	private AudioListener listener;
	
	public AudioPlayer(AudioListener listener) {
		this.listener = listener;		
	}
	
	public AudioPlayer() {
		this(null);
	}
	
	public void play(String filename) {
		try {
			final Clip clip = AudioSystem.getClip();
			clip.addLineListener(new LineListener() {
				
				@Override
				public void update(LineEvent evt) {
					if (evt.getType() == LineEvent.Type.STOP) {
						clip.close();
						if (listener != null)
							listener.soundPlayed();
					}
					
				}
			});
			clip.open(AudioSystem.getAudioInputStream(
					AudioPlayer.class.getResource("/sounds/" + filename)));
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
