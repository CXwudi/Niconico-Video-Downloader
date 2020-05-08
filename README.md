# Niconico-Video-Downloader
The coolest project I wrote that use selenium, youtube-dl to fetch videos list from niconico account favourite list and download these videos.

![alt text](https://upload.wikimedia.org/wikipedia/de/c/ce/NicoNicoDouga-Logo-Vector.svg)

# Appreciation 
Special thanks for the 3rd party programable video downloader------Youtube-dl,
without this excutable file, I can't fix the issue that happened after the niconico website updates.
Youtube-dl: https://github.com/rg3/youtube-dl/

Thanks for Selenium, without this amazing library, I can't come up with this project.
Selenium: https://www.seleniumhq.org/

And also thanks for the ChromeDriver that provided by Google
ChromeDriver: https://sites.google.com/a/chromium.org/chromedriver/

Currently this project only supports Windows and only support GoogleChrome, since I only use them

# How to use
## Prerequisite:
 - Windows OS
 - Java 11
 - Chrome browser
## Step to setup
 - download the artifact from release pages, unpack it 
 - create your own profile: go to the `data/` folder, create an copy of `example_user.properties`, and fill in that property file. 
   - modify the `data.downloadedList` to something else to avoid using the prefilled `download.txt` in `data/`
 - modify `system_config.property`
   - modify `user.config` to use your own profile
   - modify `idm` property valye if you plan to use IDM to download PVs (which means you set `downloadMethod` to "idm")
 - (optional, recommended) update youtube-dl.exe in `lib/` with command 'youtube-dl.exe --update'
 - run: open terminal in the project root folder, type `java -jar .\<the jar filename>`
 
 # Known Issue:
 - recent Chrome 80 webdriver would [flash timeout warning](https://stackoverflow.com/questions/60114639/timed-out-receiving-message-from-renderer-0-100-log-messages-using-chromedriver) and occasionally throw `ConnectException`, if that happens, simply clean out all `Runtime Broker` in Windows Task Manager, clean up chromedriver.exe, and restart the program.
