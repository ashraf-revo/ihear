import json

import requests

from Listener import Listener


def read(file):
    with open(file) as json_file:
        return json.load(json_file)


def login(host, key):
    result = requests.post("http://" + host + '/loginx', json=key)
    if result.status_code == 200:
        if result.cookies.__contains__("SESSION"):
            return result.cookies.get("SESSION")


if __name__ == "__main__":
    ihear = read("ihear.json")
    if ihear:
        SESSION = login(ihear['host'], read(ihear['key']))
        if SESSION:
            listener = Listener(ihear, SESSION)
            listener.listen()
