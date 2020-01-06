from time import sleep

from Available import Available
from Closeable import Closeable


class Thread(Closeable, Available):
    def available(self):
        self.___available = True

    def close(self):
        print("close Thread")
        self.___available = False

    def __init__(self, param):
        self.___param = param

    def sleep(self):
        if self.___available:
            print("SLEEP ", self.___param["time"])
            sleep(self.___param["time"])
