import json

import websocket

from Schema import Schema


class Listener:

    def __init__(self, ihear):
        websocket.enableTrace(False)
        self.___ws = websocket.WebSocketApp(ihear['wsUrl'],
                                            on_open=(lambda ws: self.___on_open(self)),
                                            on_message=(lambda ws, msg: self.___on_message(self, msg)),
                                            on_error=(lambda ws, msg: self.___on_error(self, msg)),
                                            on_close=(lambda ws: self.___on_close(self)),
                                            header=ihear['cookie']
                                            )
        self.___schema = Schema(ihear['schema']['event'], ihear)

    def ___on_open(self, ws):
        self.___schema.execute_on_load()

    def ___on_message(self, ws, message):
        self.___schema.execute_on_key(json.loads(message))

    def ___on_error(self, ws, error):
        self.___schema.close()

    def ___on_close(self, ws):
        self.___schema.close()

    def listen(self):
        self.___ws.run_forever()


def init_config(ihear, result):
    secure_ws = ("wss" if ihear['secure'] is True else "ws")
    secure_http = ("https" if ihear['secure'] is True else "http")
    ihear['streamUrl'] = ihear['streamUrl'].replace(":session", result.cookies.get("SESSION")) + ihear['streamId']
    ihear['streamIdUrl'] = secure_http + "://" + ihear['host'] + "/pi/stream/" + ihear['streamId']
    ihear['schemaUrlPrefix'] = secure_http + "://" + ihear['host'] + "/pi/schema/"
    ihear['wsUrl'] = secure_ws + "://" + ihear['host'] + "/echo/user/" + ihear['streamId']
    ihear['cookie'] = {'Cookie: SESSION=' + result.cookies.get("SESSION")}
    ihear['schema'] = {}
    return ihear
