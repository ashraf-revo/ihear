import json

import requests

from Listener import Listener, init_config


def read(path):
    with open(path) as json_file:
        return json.load(json_file)


def login(ihear, key):
    url = ("https" if ihear['secure'] is True else "http") + "://" + ihear['host']
    try:
        user = requests.get(url + '/user')
        return requests.post(url + '/loginx', json=key, cookies=user.cookies,
                             headers={"X-XSRF-TOKEN": user.cookies["XSRF-TOKEN"]})
    except requests.exceptions.ConnectionError:
        pass


def add_schema(ihear, login):
    get = requests.get(ihear['streamIdUrl'], cookies=login.cookies)
    requests_get = requests.get(ihear['schemaUrlPrefix'] + json.loads(get.content)['schemaId'], cookies=login.cookies)
    ihear['schema'] = json.loads(requests_get.content)
    return ihear


if __name__ == "__main__":
    ihear = read("ihear.json")
    if ihear:
        login = login(ihear, read(ihear['key']))
        if login and login.status_code is 200:
            config = add_schema(init_config(ihear, login), login)
            listener = Listener(config).listen()
