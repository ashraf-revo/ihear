import json

import websocket

from Stream import Stream


class Listener:

    def __init__(self, ihear, SESSION):
        websocket.enableTrace(False)
        self.___ihear = ihear
        self.___SESSION = SESSION
        self.___ws = websocket.WebSocketApp("ws://" + ihear['host'] + "/echo",
                                            on_open=(lambda ws: self.___on_open(self)),
                                            on_message=(lambda ws, msg: self.___on_message(self, msg)),
                                            on_error=(lambda ws, msg: self.___on_error(self, msg)),
                                            on_close=(lambda ws: self.___on_close(self)),
                                            header={'Cookie: SESSION=' + SESSION}
                                            )

    def ___on_open(self, ws):
        self.___stream = Stream(self.___ihear, self.___SESSION)

    def ___on_message(self, ws, result):
        message = json.loads(result)
        if message['resource'] == 'CAMERA':
            if message['action'] == 'record':
                self.___stream.record()
            if message['action'] == 'teardown':
                self.___stream.teardown()
        if message['resource'] == 'GPS':
            if message['action'] == 'retrieve':
                print('retrieve GPS')
        if message['resource'] == 'GPIO':
            if message['action'] == 'open':
                print('open GPIO' + message['pin'])
            if message['action'] == 'close':
                print('close GPIO' + message['pin'])

    def ___on_error(self, ws, error):
        self.___stream.teardown()

    def ___on_close(self, ws):
        self.___stream.teardown()

    def listen(self):
        self.___ws.run_forever()
