# ExoPlayerDemo

ExoPlayerDemo is an simple Android application which is used to play video using Exoplayer.

App contains 3 screens:
* 1. Home Screen: In Home screen will trigger the Jobintent service to cache the videos by passing video Urls.  Based on user click will take to 2 different players.

* 2. PlayerWithCacheActivity: If video is cached it will be played from cache else from network

* 3. PlayerActivity: Video will play always from the network.

* 4. VideoCacheService: Used to cache the video.
