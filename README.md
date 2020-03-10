# Ihear
![Build](https://travis-ci.org/ashraf-revo/ihear.svg?branch=master)

### what is used for?
- it help you to make live stream from your raspberry pi camera
- it help you to control you raspberry pi using your mobile and your joystick
  - signal GPIO (HIGH,LOW,TOGGLE,BLINK) to Pins
  - signal PROCESS (SLEEP)
  - signal CAMERA (SHOT,RECORD,STREAM,TEARDOWN)
  - signal HEALTH (ACK,NAK)

### Structure of the system
- [ihear](https://github.com/ashraf-revo/ihear/README.md)
    - the main core of the system that provides ([auth](auth/README.md),[pi](pi/README.md),[ui](ui/README.md)) micro services
- [spy](https://github.com/ashraf-revo/spy/README.md) 
    - python application run on raspberry pi listen to your actions 

![Alt text](ihear/images/ihear.png?raw=true)


### your steps to run it on GKE
- open your Cloud Shell
- type ```git clone https://github.com/ashraf-revo/ihear```
- type ```cd ihear/ihear```
- type ```kubectl apply -f ui-svc.yaml```
- type ```kubectl get svc```
- copy ip of ui service and past it to base/ihear.yaml in the node property of default config map
- type ```kubectl apply -f base/.```
- wait 1 miniutes
- type ```kubectl apply -f ui-dep.yaml```
