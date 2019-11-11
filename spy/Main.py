import json
import requests

from Listener import Listener


def read(file):
    with open(file) as json_file:
        return json.load(json_file)


def login(ihear, key):
    result = requests.post(("https" if ihear['secure'] == True else "http") + "://" + ihear['host'] + '/loginx', json=key)
    if result.status_code == 200:
        if result.cookies.__contains__("SESSION"):
            return result.cookies.get("SESSION")


if __name__ == "__main__":
    ihear = read("ihear.json")
    if ihear:
        SESSION = login(ihear, read(ihear['key']))
        print SESSION
        if SESSION:
            listener = Listener(ihear, SESSION)
            listener.listen()
