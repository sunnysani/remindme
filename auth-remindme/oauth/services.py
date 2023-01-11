from django_grpc_framework.services import Service
from .models import UserAccount
from .serializers import UserProtoSerializer
import jwt
from auth_remindme.settings import SIMPLE_JWT

class Resource(Service):
    def GetResource(self, request, context):
        metadict = dict(context.invocation_metadata())
        access_token = metadict['authorization']
        access_token = access_token.split(' ')[1]
        print(access_token)

        user_id = ''
        try:
            valid_data = jwt.decode(access_token, SIMPLE_JWT['SIGNING_KEY'], algorithms=[SIMPLE_JWT['ALGORITHM']],)
            user_id = valid_data['user_id']
        except Exception as v:
            print("validation error", v)

        print(user_id)
        user = UserAccount.objects.get(id=user_id)
        serializer = UserProtoSerializer(user)
        print(serializer.message)
        return serializer.message
