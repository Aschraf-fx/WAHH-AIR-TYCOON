# Tycoon WAHH AIR!

English offline Android business game, currency MYR. Package `com.wahhair.tycoon`, Android 8+ with an up-to-date Android System WebView. A separate game from the WAHH AIR business records app. No real business backup is bundled.

Start with RM500 at a folding table at home in Senawang. Progress through Home Business, Rider Sales Team, Street Stall, Popular Kiosk, Beverage Shop, Branch Network, and Seremban Tycoon. The six district game board is a stylized illustration of Seremban, not an accurate street map or navigation tool. District economics and events are fictional.

## Play
1. Kitchen: mix 20 bottles; press 1× to run the mixer.
2. Open business. Walk-ins buy automatically while game time advances.
3. Restock ingredients, choose supplier, recipe and price; maintain freshness.
4. Sell 20 bottles and upgrade in Growth to hire riders. Riders carry allocated stock, sell, earn commission and return unsold drinks.
5. Unlock equipment, staff, training, marketing, bulk orders and district branches.
6. Win by reaching level 7, all six districts, 85 reputation and seven consecutive profitable days. Continue in Endless Mode.

A game day takes six real minutes at 1×, with pause/2×/4× controls. Each day closes at 20:00, settles costs and pauses for planning. Time pauses in the background; there is no offline income. Cash is fictional MYR, no real-money purchases or ads. Use the one-time RM500 recovery loan if needed.

Founder supports gallery photo selection, crop/zoom, profile name and photo removal. Native Android photo decoding handles EXIF orientation and downsamples before showing the crop. Photos stay in the local save and only leave the device if the player exports/shares their save. Inbox is scripted game dialogue, not multiplayer chat.

## Economy
Production consumes ingredients and captures batch costs. Purchases add inventory, not a second profit expense. Sales recognize cost of goods sold. Expired bottles become waste expense. Riders reserve stock at dispatch and pay RM1 per sold bottle commission. All branches share central ready stock; a manager can automate mixing, but purchasing and dispatch remain player actions. Recipes and prices affect customers. Powder suppliers affect cost and taste quality. Completed-day profit is allocated once between retention and a 50:50 Wan/Husein payout; distributions are not expenses. There is no tax, interest, staff dismissal, branch closure or real economic forecast in this game.

## Build and verify
`node tests/engine.test.cjs` runs simulation tests. `npm install` then `npx playwright install chromium` and `npm run test:ui` run browser gameplay tests and capture screenshots. `gradle :app:assembleDebug` with Gradle8.9, JDK17, Android SDK35 builds the APK. GitHub Actions executes the tests, compiles the APK, verifies its signature and uploads an artifact with screenshots. Current validation status is available on the Actions run, not inferred from this README.

Debug signing key is cached between CI builds. Export a game backup before replacing an installation: if a cache is evicted, Android may reject an update signed by a new key. Permanent production signing should be configured before public store distribution. This is a privately distributed playable game, not a Play Store release.

Game source uses Canvas 2D artwork and local assets only; no third-party map tiles or runtime network calls. Landscape orientation is preferred; narrow windows remain supported. All gameplay text is English.

## v1.1 — offline music
Wahh Air Intro plays on launch. Starting a new game moves to Wahh Air!; otherwise the intro completes first. Background order is Wahh Air! → supplied BIGBANG playlist → Wahh Air!, indefinitely. The intro is not repeated in the background loop. Founder has volume, mute and next-track controls; sale sound effects retain a separate switch. Music pauses on backgrounding/audio focus loss and resumes at its position when returning. Music tempo is independent of game speed.

Audio is bundled offline. Playlist is complete (about 1h47m), encoded AAC 96kbps; the two MP3 files are unchanged. Playlist source parts are reconstructed and SHA256-checked by scripts/assemble_audio.py before compilation. Android uses one native MediaPlayer with audio focus, so browser autoplay restrictions do not block launch audio. No game save data is reset; v1 saves gain default music settings. CI compares the signing certificate to v1.0 before publishing the APK.
