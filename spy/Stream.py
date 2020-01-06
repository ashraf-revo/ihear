import os
import threading


class Stream:
    def __init__(self, ihear, result):
        self.___streamUrl = ihear['streamUrl'].replace(":session", result.cookies.get("SESSION"))+ihear['streamId']
        self.___fun = ihear['fun']
        self.teardown()

    def ___record(self):
        threading.Thread(target=self.___start).start()

    def teardown(self):
        os.system("pkill -9 ffmpeg")

    def ___start(self):
        cmd = "ffmpeg -nostdin -re -i  /dev/video0  -r 15 -c:v h264 -an -vf format=yuv420p  " \
              "-rtsp_transport tcp -threads 2 -quality realtime -preset ultrafast -deadline .01 -tune zerolatency   " \
              "-f " + self.___fun + " " + self.___fun + "://" + self.___streamUrl + " &"
        os.system(cmd)

    def handle(self, message):
        if message['action'] == 'record':
            self.___record()
        if message['action'] == 'teardown':
            self.teardown()
