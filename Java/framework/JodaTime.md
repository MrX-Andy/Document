#### JodaTime  

● gradle  
compile 'joda-time:joda-time:2.9.9'  

● sample  
final String defaultFormat = "yyyy-MM-dd HH:mm:ss:SSS";  
final String aFormat = "yyyy-MM-dd HH:mm:ss";  
final String bFormat = "yyyy-MM-dd HH:mm";  

LogTrack.w(new DateTime(2000, 1, 1, 0, 0, 0, 0).toString(defaultFormat));  
LogTrack.w(new DateTime().plusDays(-1).toString(aFormat));  
LogTrack.w(new DateTime().plusDays(1).toString(aFormat));  
LogTrack.w(new DateTime().minusDays(-1).toString(aFormat));  
LogTrack.w(new DateTime().minusDays(1).toString(aFormat));  


  
