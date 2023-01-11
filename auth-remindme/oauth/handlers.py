from .services import Resource
from oauth_proto import resource_pb2_grpc


def grpc_handlers(server):
    resource_pb2_grpc.add_ResourceServiceServicer_to_server(Resource.as_servicer(), server)