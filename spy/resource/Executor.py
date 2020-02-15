from domain.Schema import ResourceType, ActionType
from resource.Camera import Camera
from resource.PiResource import PiResource
from resource.Pin import Pin
from resource.Socket import Socket
from resource.Thread import Thread


class Executor(PiResource):
    async def handel(self, actionType, data):
        pass

    ___resources = {}

    def __init__(self, notify):
        self.___notify = notify
        self.___parse()

    async def available(self):
        for c in self.___resources.values():
            await c.available()

    async def close(self):
        for c in self.___resources.values():
            await c.close()

    def ___parse(self):
        notify_ = {"notify": self.___notify}
        self.___resources[ResourceType.SOCKET] = Socket(notify_)
        self.___resources[ResourceType.THREAD] = Thread(notify_)
        self.___resources[ResourceType.CAMERA] = Camera(notify_)
        self.___resources[ResourceType.PIN] = Pin(notify_)

    async def call(self, key):
        for action in key.actions:
            await self.___resources[ResourceType[action.resourceType]].handel(ActionType[action.actionType],
                                                                              action.data)
