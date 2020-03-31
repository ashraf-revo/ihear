from time import sleep

from resource.PiResource import PiResource
from domain.Schema import ActionType


class Thread(PiResource):
    async def handel(self, actionType, data):
        if actionType == ActionType.SLEEP:
            await self.___sleep(data)
        pass

    async def available(self):
        self.___available = True

    async def close(self):
        print("close Thread")
        self.___available = False

    def __init__(self, param):
        self.___param = param
        self.___available = True

    async def ___sleep(self, data):
        if self.___available:
            print("SLEEP ", data["Time"])
