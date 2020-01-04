# streamer

### what is used for?
- it accept sdp ,extract sps pps and store them 
- route message from rabbitmq based on streamId

### your steps to use it
- you have to install `rabbitmq` and `ffmpeg`
- run it using `mvn clean spring-boot:run`
- start streaming using `ffplay -fflags nobuffer  -i http://{ip}:8083/video/{streamId}`
