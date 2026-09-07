const vm=require('node:vm'),fs=require('node:fs'),assert=require('node:assert/strict');let audios=[];
class FakeAudio{constructor(src){this.src=src;this.paused=true;audios.push(this)}play(){this.paused=false;return Promise.resolve()}pause(){this.paused=true}}
const context={window:{},document:{hidden:false,addEventListener(){}},Audio:FakeAudio};vm.createContext(context);vm.runInContext(fs.readFileSync('app/src/main/assets/music.js','utf8')+';globalThis.M=Music',context);let m=context.M;
m.configure({musicEnabled:true,musicVolume:45});assert.equal(m.title(),'Wahh Air Intro');assert.equal(audios.at(-1).volume,.45);
m.enterGame();assert.equal(m.title(),'Wahh Air!');assert.equal(audios[0].paused,true);
audios.at(-1).onended();assert.equal(m.title(),'BIGBANG Playlist');audios.at(-1).onended();assert.equal(m.title(),'Wahh Air!');
for(let i=0;i<8;i++){audios.at(-1).onended();assert.notEqual(m.title(),'Wahh Air Intro')}
m.configure({musicEnabled:false,musicVolume:30});assert.equal(audios.at(-1).paused,true);assert.equal(audios.at(-1).volume,.3);
m.configure({musicEnabled:true,musicVolume:30});assert.equal(audios.at(-1).paused,false);m.pause();assert.equal(audios.at(-1).paused,true);m.resume();assert.equal(audios.at(-1).paused,false);
assert.equal(audios.filter(a=>!a.paused).length,1);console.log('PASS music: launch intro, theme/playlist order, repeated loop, single player, volume, mute and background pause/resume');
