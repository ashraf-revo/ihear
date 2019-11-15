import json
import requests

from Listener import Listener


def read(file):
    with open(file) as json_file:
        return json.load(json_file)


def login(ihear, key):
    url = ("https" if ihear['secure'] == True else "http") + "://" + ihear['host']
    user = requests.get(url + '/user')
    return requests.post(url + '/loginx', json=key, cookies=user.cookies,
                         headers={"X-XSRF-TOKEN": user.cookies["XSRF-TOKEN"]})


if __name__ == "__main__":
    ihear = read("ihear.json")
    if ihear:
        result = login(ihear, read(ihear['key']))
        if result.status_code == 200:
            listener = Listener(ihear, result).listen()
