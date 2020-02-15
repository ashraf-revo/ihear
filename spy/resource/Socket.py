import psutil

from resource.PiResource import PiResource
from domain.Schema import ActionType


class Socket(PiResource):
    async def handel(self, actionType, data):
        if actionType == ActionType.ACK:
            await self.___ack(data)
        if actionType == ActionType.NAK:
            await self.___nak(data)
        pass

    async def available(self):
        self.___available = True

    async def close(self):
        print("close Socket")
        self.___available = False

    def __init__(self, param):
        self.___param = param
        self.___available = True

    async def ___ack(self, data):
        if self.___available:
            print("ACK ", "OK")
            self.___param["notify"](
                {"action": "ACK", "status": "OK",
                 "metrics": {"mem": {"percent": psutil.virtual_memory().percent},
                             "cpu": {"percent": psutil.cpu_percent()}}})

    async def ___nak(self, data):
        if self.___available:
            print("NAK ", "FAIL")
            self.___param["notify"]({"action": "NAK", "status": "FAIL"})
