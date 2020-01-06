import threading

from Available import Available
from Camera import Camera
from Closeable import Closeable
from Pin import Pin
from Thread import Thread


class Schema(Closeable, Available):
    def available(self):
        for c in self.___resources.values():
            c.available()

    def close(self):
        for c in self.___resources.values():
            c.close()

    def __init__(self, schema, param):
        self.___schema = schema
        self.___param = param
        self.___resources = {}
        self.___parse()
        self.available()

    def ___parse(self):
        if self.___schema and self.___schema['resources']:
            for r in self.___schema['resources']:
                if str.startswith(str(r['resourceType']), "PIN"):
                    pin = str.replace(str(r['resourceType']), "PIN_", "")
                    self.___resources[str(r['resourceType'])] = Pin({"pin": pin})
                if str(r['resourceType']) == "CAMERA":
                    self.___resources[str(r['resourceType'])] = Camera(self.___param)
                if str(r['resourceType']) == "THREAD":
                    self.___resources[str(r['resourceType'])] = Thread({"time": 1})

    def ___call(self, actions):
        for action in actions:
            getattr(self.___resources[action['resourceType']], str.lower(str(action['actionType'])))()

    def ___run_or_start(self, listener):
        thread = threading.Thread(target=self.___call, args=(listener['actions'],))
        if listener['threading']:
            thread.start()
        else:
            thread.run()

    def execute_on_load(self):
        if self.___schema and self.___schema['listeners']:
            for listener in self.___schema['listeners']:
                if listener['listenerType'] == "LOOAD":
                    self.___run_or_start(listener)

    def execute_on_key(self, message):
        if self.___schema and self.___schema['keys']:
            for k in self.___schema['keys']:
                if k['keyType'] == message['keyType']:
                    for listener in k['listeners']:
                        if listener['listenerType'] == message['listenerType']:
                            self.___run_or_start(listener)
