# This program doesn't work any more

### Background

Program that would allow the user to feed in a text file with a list of company/client names and get their addresses in a regular format from Google Maps.

### Why it sucks

It wasn't anything too clever - in fact it was almost too stupid. It literally did a Google Maps search, then read the resulting page's source. It doesn't work any more because the source code of Google Maps pages is completely different now. Next time, maybe consider using the goshdarn [API](https://developers.google.com/maps/).

### How to use it

**Don't.**

But if you do, choose either [ScannerWithRegions.java](GoogleMapsMiner/ScannerWithRegions.java) *or* [ScannerAll.java](GoogleMapsMiner/ScannerAll.java) as the main method in your [MANIFEST.MD](MANIFEST.MD), and make sure to hard-code the main method of whichever one you chose to contain the path to a text file with a list of company information. It will then fail to get anything from Google Maps because the Google Maps source code has changed.
