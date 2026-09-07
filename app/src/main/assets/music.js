'use strict';
const Music=(()=>{const tracks=['audio/intro.mp3','audio/wahh-air.mp3','audio/playlist.m4a'],titles=['Wahh Air Intro','Wahh Air!','BIGBANG Playlist'];let index=0,player=null,enabled=true,volume=45,active=true,error='';const native=()=>window.Android&&typeof Android.musicOptions==='function';const nextIndex=i=>i===1?2:1;
function play(){if(!player||!enabled||!active)return;let promise=player.play();if(promise)promise.catch(()=>{error='Tap Play music to enable audio.'})}
function load(i){if(player){player.pause();player.onended=null;player.onerror=null;}index=i;error='';player=new Audio(tracks[i]);player.preload='metadata';player.volume=volume/100;player.onended=()=>load(nextIndex(index));player.onerror=()=>{error='Music unavailable in this preview. The Android app includes all tracks.'};play()}
function configure(s){enabled=s.musicEnabled!==false;volume=Number.isFinite(s.musicVolume)?Math.max(0,Math.min(100,s.musicVolume)):45;if(native()){Android.musicOptions(enabled,volume);return;}if(!player)load(0);player.volume=volume/100;if(enabled)play();else player.pause()}
function enterGame(){if(native())Android.musicEnterGame();else if(index===0)load(1)}
function next(){if(native())Android.musicNext();else load(nextIndex(index))}
function resume(){active=true;if(!native())play()}
function pause(){active=false;if(!native()&&player)player.pause()}
function title(){return native()?Android.musicTitle():titles[index]}
function problem(){return native()?Android.musicError():error}
document.addEventListener('visibilitychange',()=>document.hidden?pause():resume());document.addEventListener('pointerdown',()=>{if(!native()&&error)play()},{passive:true});
return{configure,enterGame,next,resume,pause,title,problem,nextIndex};})();
