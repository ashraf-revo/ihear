from Available import Available
from Closeable import Closeable


class Socket(Closeable, Available):
    def available(self):
        self.___available = True

    def close(self):
        print("close Socket")
        self.___available = False

    def __init__(self, param):
        self.___param = param
        self.___available = False

    def ack(self):
        if self.___available:
            print("ACK ", "OK")
            self.___param["notify"]({"status": "OK"})

    def nak(self):
        if self.___available:
            print("NAK ", "FAIL")
            self.___param["notify"]({"status": "FAIL"})
