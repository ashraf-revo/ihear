# livePoll

### what is used for?
- it accept rtsp stream
- anallize sdp and identifiy rtp messages
- depackage h.264 video ,depackage aac audio from rtp payload
- send h264/aac to rabbitmq queue

### your steps to use it
- you have to install `rabbitmq` and `ffmpeg`
- run it using `mvn clean spring-boot:run`
- start streaming using `ffmpeg -re -i  /dev/video0 -vcodec libx264 -r 15 -pix_fmt yuv420p -profile:v baseline  -rtsp_transport tcp -threads 2 -quality realtime -preset ultrafast -deadline .001 -tune zerolatency  -an -f rtsp rtsp://{ip}:8086/stream/{session}/{streamId}`
