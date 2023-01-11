# Generated by the gRPC Python protocol compiler plugin. DO NOT EDIT!
"""Client and server classes corresponding to protobuf-defined services."""
import grpc

from oauth_proto import resource_pb2 as oauth__proto_dot_resource__pb2


class ResourceServiceStub(object):
    """Missing associated documentation comment in .proto file."""

    def __init__(self, channel):
        """Constructor.

        Args:
            channel: A grpc.Channel.
        """
        self.GetResource = channel.unary_unary(
                '/oauth_proto.ResourceService/GetResource',
                request_serializer=oauth__proto_dot_resource__pb2.ResourceRequest.SerializeToString,
                response_deserializer=oauth__proto_dot_resource__pb2.ResourceResponse.FromString,
                )


class ResourceServiceServicer(object):
    """Missing associated documentation comment in .proto file."""

    def GetResource(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')


def add_ResourceServiceServicer_to_server(servicer, server):
    rpc_method_handlers = {
            'GetResource': grpc.unary_unary_rpc_method_handler(
                    servicer.GetResource,
                    request_deserializer=oauth__proto_dot_resource__pb2.ResourceRequest.FromString,
                    response_serializer=oauth__proto_dot_resource__pb2.ResourceResponse.SerializeToString,
            ),
    }
    generic_handler = grpc.method_handlers_generic_handler(
            'oauth_proto.ResourceService', rpc_method_handlers)
    server.add_generic_rpc_handlers((generic_handler,))


 # This class is part of an EXPERIMENTAL API.
class ResourceService(object):
    """Missing associated documentation comment in .proto file."""

    @staticmethod
    def GetResource(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/oauth_proto.ResourceService/GetResource',
            oauth__proto_dot_resource__pb2.ResourceRequest.SerializeToString,
            oauth__proto_dot_resource__pb2.ResourceResponse.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)
