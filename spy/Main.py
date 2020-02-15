from Listener import Listener
from domain.Env import Env
from util.Oauth import Oauth

if __name__ == "__main__":
    env = Env.read("ihear.key")
    oauth = Oauth(env)
    oauth.login()
    Listener(env, oauth).listen()
