from domain.Schema import ActionType
from resource.PiResource import PiResource


class Pin(PiResource):
    async def handel(self, actionType, data):
        if actionType == ActionType.ON:
            await self.___on(data)
        if actionType == ActionType.OFF:
            await self.___off(data)
        if actionType == ActionType.TOGGLE:
            await self.___toggle(data)
        if actionType == ActionType.BLINK:
            await self.___blink(data)
        pass

    async def available(self):
        self.___available = True

    async def close(self):
        print("close Pin")
        self.___available = False

    def __init__(self, param):
        self.___param = param
        self.___available = True

    async def ___on(self, data):
        if self.___available:
            print("ON ", "Pin " + data["Pin"])

    async def ___off(self, data):
        if self.___available:
            print("OFF ", "Pin " + data["Pin"])

    async def ___toggle(self, data):
        if self.___available:
            print("TOGGLE ", "Pin " + data["Pin"])

    async def ___blink(self, data):
        if self.___available:
            print("BLINK ", "Pin " + data["Pin"])
