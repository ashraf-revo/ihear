from Available import Available
from Closeable import Closeable


class Camera(Closeable, Available):
    def available(self):
        self.___available = True

    def close(self):
        print("close Camera")
        self.teardown()
        self.___available = False
        pass

    def __init__(self, param):
        self.___param = param
        self.___available = False

    def record(self):
        if self.___available:
            print("RECORD ", "Camera")
            # cmd = "ffmpeg -nostdin -re -i  /dev/video0  -r 20 -c:v h264 -an -vf format=yuv420p  " \
            # "-rtsp_transport tcp -threads 2 -quality realtime -preset ultrafast -deadline .01 -tune zerolatency   "
            # \ "-f " + self.___param['fun'] + " " + self.___param['fun'] + "://" + self.___param['streamUrl'] + " &"
            self.___available = False
            # os.system(cmd)

    def teardown(self):
        print("TEARDOWN ", "Camera")
        # os.system("pkill -9 ffmpeg")
        self.___available = True
