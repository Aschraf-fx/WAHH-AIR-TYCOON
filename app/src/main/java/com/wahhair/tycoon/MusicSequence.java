package com.wahhair.tycoon;
/** Intro is a launch cue, not part of the background loop. */
public final class MusicSequence {
 public static final int INTRO=0, THEME=1, PLAYLIST=2;
 public static int next(int track){return track==THEME?PLAYLIST:THEME;}
 public static String file(int track){return track==INTRO?"audio/intro.mp3":track==THEME?"audio/wahh-air.mp3":"audio/playlist.m4a";}
 public static String title(int track){return track==INTRO?"Wahh Air Intro":track==THEME?"Wahh Air!":"BIGBANG Playlist";}
}
