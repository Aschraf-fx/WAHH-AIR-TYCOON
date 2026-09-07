package com.wahhair.tycoon;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;

/** One native player at a time, using packaged assets and foreground audio focus. */
public final class GameMusic {
 private final Activity activity;
 private final SharedPreferences prefs;
 private final AudioManager manager;
 private final AudioAttributes attributes=new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
 private final AudioFocusRequest focus;
 private MediaPlayer player;
 private boolean foreground=false,ready=false,enabled=true,hasFocus=false,ducked=false,released=false;
 private volatile int track=MusicSequence.INTRO;
 private float volume=.45f;
 private volatile String error="";
 public GameMusic(Activity a){activity=a;prefs=a.getPreferences(Context.MODE_PRIVATE);manager=(AudioManager)a.getSystemService(Context.AUDIO_SERVICE);enabled=prefs.getBoolean("musicEnabled",true);volume=prefs.getFloat("musicVolume",.45f);
 focus=new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(attributes).setOnAudioFocusChangeListener(change->{
  if(change==AudioManager.AUDIOFOCUS_GAIN){hasFocus=true;ducked=false;applyVolume();startIfReady();}
  else if(change==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){ducked=true;applyVolume();}
  else{hasFocus=false;pause();}
 }).build();}
 private void load(int next){if(released)return;disposePlayer();track=next;error="";MediaPlayer p=new MediaPlayer();player=p;p.setAudioAttributes(attributes);p.setOnPreparedListener(mp->{if(mp!=player||released)return;ready=true;applyVolume();startIfReady();});p.setOnCompletionListener(mp->{if(mp==player&&!released)load(MusicSequence.next(track));});p.setOnErrorListener((mp,what,extra)->{if(mp==player){error="Music could not play. Tap Next track to retry.";ready=false;}return true;});
 try(AssetFileDescriptor fd=activity.getAssets().openFd(MusicSequence.file(track))){p.setDataSource(fd.getFileDescriptor(),fd.getStartOffset(),fd.getLength());p.prepareAsync();}catch(Exception e){error="Music file could not load.";disposePlayer();}}
 private void applyVolume(){if(player!=null){float v=volume*(ducked?.2f:1f);try{player.setVolume(v,v);}catch(IllegalStateException ignored){}}}
 private void requestFocus(){if(enabled&&foreground&&!hasFocus)hasFocus=manager.requestAudioFocus(focus)==AudioManager.AUDIOFOCUS_REQUEST_GRANTED;}
 private void startIfReady(){if(foreground&&enabled&&ready&&hasFocus&&player!=null){try{player.start();}catch(IllegalStateException e){error="Music could not start.";}}}
 private void pause(){if(player!=null&&ready){try{if(player.isPlaying())player.pause();}catch(IllegalStateException ignored){}}}
 private void disposePlayer(){ready=false;if(player!=null){player.setOnCompletionListener(null);player.setOnPreparedListener(null);player.setOnErrorListener(null);player.release();player=null;}}
 public void resume(){if(released)return;foreground=true;requestFocus();if(player==null)load(track);else startIfReady();}
 public void suspend(){foreground=false;pause();if(hasFocus)manager.abandonAudioFocusRequest(focus);hasFocus=false;}
 public void setOptions(boolean on,int percent){enabled=on;volume=Math.max(0,Math.min(100,percent))/100f;prefs.edit().putBoolean("musicEnabled",enabled).putFloat("musicVolume",volume).apply();applyVolume();if(on){requestFocus();if(player==null&&foreground)load(track);else startIfReady();}else{pause();if(hasFocus)manager.abandonAudioFocusRequest(focus);hasFocus=false;}}
 public void enterGame(){if(track==MusicSequence.INTRO){requestFocus();load(MusicSequence.THEME);}}
 public void next(){requestFocus();load(MusicSequence.next(track));}
 public String title(){return MusicSequence.title(track);}
 public String error(){return error;}
 public void release(){suspend();released=true;disposePlayer();}
}
