from oauth2_client.credentials_manager import CredentialManager, ServiceInformation


class Oauth:
    ___scope = ['read', 'write']
    access_token = ''

    def __init__(self, env):
        self.___manager = CredentialManager(ServiceInformation(env.oauth.authorize,
                                                               env.oauth.token,
                                                               env.oauth.clintId,
                                                               env.oauth.clientSecret,
                                                               self.___scope))

    def login(self):
        self.___manager.init_with_client_credentials()
        self.access_token = self.___manager._access_token
