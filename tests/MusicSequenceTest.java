import com.wahhair.tycoon.MusicSequence;
public class MusicSequenceTest {
 public static void main(String[] args){int t=MusicSequence.INTRO;if(!MusicSequence.file(t).endsWith("intro.mp3"))throw new AssertionError();t=MusicSequence.next(t);if(t!=MusicSequence.THEME)throw new AssertionError();for(int i=0;i<100;i++){t=MusicSequence.next(t);if(t!=(i%2==0?MusicSequence.PLAYLIST:MusicSequence.THEME))throw new AssertionError();}System.out.println("PASS native sequence: intro once, theme and playlist loop 100 transitions");}
}
