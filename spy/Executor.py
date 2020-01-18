import threading

from Available import Available
from Camera import Camera
from Closeable import Closeable
from Pin import Pin
from Schema import ListenerType, ResourceType, KeyType
from Thread import Thread


class Executor(Closeable, Available):
    ___resources = {}

    def __init__(self, schema, notify):
        self.___schema = schema
        self.___parse()
        self.available()

    def available(self):
        for c in self.___resources.values():
            c.available()

    def close(self):
        for c in self.___resources.values():
            c.close()

    def ___parse(self):
        if self.___schema and self.___schema.event.resources:
            for r in self.___schema.event.resources:
                if str.startswith(str(r.resourceType), "PIN"):
                    self.___resources[ResourceType[r.resourceType]] = Pin(
                        {"pin": str.replace(str(r.resourceType), "PIN_", "")})
                if ResourceType[r.resourceType] == ResourceType.CAMERA:
                    self.___resources[ResourceType[r.resourceType]] = Camera(None)
                if ResourceType[r.resourceType] == ResourceType.THREAD:
                    self.___resources[ResourceType[r.resourceType]] = Thread({"time": 1})

    def ___call(self, actions):
        for action in actions:
            getattr(self.___resources[ResourceType[action.resourceType]], str.lower(str(action.actionType)))()

    def ___run_or_start(self, listener):
        thread = threading.Thread(target=self.___call, args=(listener.actions,))
        if listener.threading:
            thread.start()
        else:
            thread.run()

    def execute_on_load(self):
        if self.___schema and self.___schema.event.listeners:
            for listener in self.___schema.event.listeners:
                if ListenerType[listener.listenerType] == ListenerType.LOOAD:
                    self.___run_or_start(listener)

    def execute_on_key(self, call):
        if self.___schema and self.___schema.event.keys:
            for k in self.___schema.event.keys:
                if KeyType[k.keyType] == KeyType[call.keyType]:
                    for listener in k.listeners:
                        if listener.listenerType == call.listenerType:
                            self.___run_or_start(listener)
