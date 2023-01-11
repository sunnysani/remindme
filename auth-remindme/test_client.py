import grpc
from oauth_proto import resource_pb2, resource_pb2_grpc

# Isi token dengan access token baru
with grpc.insecure_channel('34.71.162.16:50051') as channel:
    stub = resource_pb2_grpc.ResourceServiceStub(channel)
    print('----- GetResource -----')
    response = stub.GetResource(resource_pb2.ResourceRequest(),
        metadata=[
            ('authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNjUzNTc0MDcxLCJpYXQiOjE2NTM1NzA0NzEsImp0aSI6ImIzNGY1YTEwNjk3NTQzODg5NjVmZGI1MDIyZGIxZjZmIiwidXNlcl9pZCI6MX0.PtlrZHiTdDKdzN9MkjUkhVeQaUhbcooYaOaqtwZoz-Y'),
        ]
    )
    print(response, end='')
