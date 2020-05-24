# Pandemic Tracker
[![Build Status](https://travis-ci.com/suudupa/Pandemic-Tracker.svg?branch=dev)](https://travis-ci.com/suudupa/Pandemic-Tracker)

**Pandemic Tracker** is an Android application that lets you stay up-to-date with the latest stats and news on COVID-19 from around the globe.

**Download** the app on your Android device: https://github.com/suudupa/Pandemic-Tracker/raw/master/app/release/app-release.apk (note that you must configure your settings to allow the installation of unknown apps).

***

### Screenshots:

![image](https://user-images.githubusercontent.com/22065101/82318793-7381f280-999e-11ea-8e9e-e911122d1885.jpg)

***

### Project Overview:

• Real time statistics parsed from Worldometer (https://www.worldometers.info/coronavirus/) using PHP and stored in a JSON file on the server (https://pandemictracker.rahulkaranth.com/covid19data.json), which is fetched using AsyncTasks on the client side;  
• News headlines fetched from News API (https://newsapi.org/) - API that returns JSON metadata for live headlines and articles from around the world;  
• Uses Retrofit - REST Client for Java and Android;  
• Uses Glide - image loading and caching library for Android;  
• Uses Prettytime for converting Java Date() objects.
