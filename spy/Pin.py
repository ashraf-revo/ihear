from Available import Available
from Closeable import Closeable


class Pin(Closeable, Available):
    def available(self):
        self.___available = True

    def close(self):
        print("close Pin")
        self.___available = False

    def __init__(self, param):
        self.___param = param
        self.___available = False

    def on(self):
        if self.___available:
            print("ON ", self.___param["pin"])

    def off(self):
        if self.___available:
            print("OFF ", self.___param["pin"])

    def toggle(self):
        if self.___available:
            print("TOGGLE ", self.___param["pin"])

    def blink(self):
        if self.___available:
            print("BLINK ", self.___param["pin"])
