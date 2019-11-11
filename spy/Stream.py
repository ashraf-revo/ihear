import os
import threading


class Stream:
    def __init__(self, ihear, SESSION):
        self.___streamUrl = ihear['stream'].replace(":SESSION", SESSION)
        self.___fun = ihear['fun']

        self.teardown()

    def record(self):
        threading.Thread(target=self.___start).start()

    def teardown(self):
        os.system("pkill -9 ffmpeg")

    def ___start(self):
        cmd = "ffmpeg -nostdin -re -i  /dev/video0  -r 20 -c:v h264 -an -vf format=yuv420p  " \
              "-rtsp_transport tcp -threads 2 -quality realtime -preset ultrafast -deadline .01 -tune zerolatency   " \
              "-f " + self.___fun + " " + self.___fun + "://" + self.___streamUrl + " &"
        os.system(cmd)
