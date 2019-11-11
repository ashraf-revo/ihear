# Ihear

### what is used for?
- it help you to make live stream from your raspberry pi camera
- it help you to control you raspberry pi using your mobile and your joystick
  - (record ,teardown)  rtsp stream
  - signal GPIO (HIGH,LOW) to Pins

### Structure of the system
- [ihear](https://github.com/ashraf-revo/ihear/README.md)
    - the main core of the system that provides ([auth](auth/README.md), [livepoll](livepoll/README.md) ,[pi](pi/README.md) ,[streamer](streamer/README.md) , [ui](ui/README.md)) micro services
- [piViewr](https://github.com/ashraf-revo/piViewr/README.md)
    - android application to control you raspberry pi using joystick , show you raspberry pi stream
- [spy](https://github.com/ashraf-revo/spy/README.md) 
    - python application run on raspberry pi listen to your actions from piViewr ,rtsp to [livepoll](livepoll/README.md) directly

![Alt text](ihear/images/ihear.png?raw=true)

