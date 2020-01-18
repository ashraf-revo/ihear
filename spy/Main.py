from Env import Env
from Listener import Listener
from Oauth import Oauth

if __name__ == "__main__":
    env = Env.read("ihear.key")
    oauth = Oauth(env)
    oauth.login()
    Listener(env, oauth).listen()
