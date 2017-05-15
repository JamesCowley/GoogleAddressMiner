# This program doesn't work any more

### Background

Program that would allow the user to feed in a text file with a list of company/client names and get their addresses in a regular format from Google Maps.

### Why it sucks

* **It was poorly written:** this was the first realworld application I ever made in Java, and as such it is 80% hack and 20% luck.

* **It was poorly conceived:** no sensible [API](https://developers.google.com/maps/) implementation here, nope... It works by building a Google Maps URL string, getting the whole webpage for that search and then looking through the page source. Unsurprisingly, Google Maps has changed its layout and functionality a little bit, rendering this method utterly useless.

### How to use it

Firstly, **don't**.

But if you do, choose either [ScannerWithRegions.java](GoogleMapsMiner/ScannerWithRegions.java) *or* [ScannerAll.java](GoogleMapsMiner/ScannerAll.java) as the main method in your [MANIFEST.MD](MANIFEST.MD), and make sure to hard-code the main method of whichever one you chose to contain the path to a text file with a list of company information. It will then fail to get anything from Google Maps because the Google Maps source code has changed.
