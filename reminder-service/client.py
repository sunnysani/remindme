



# Isi token dengan access token baru
def getUsername(bearerToken):
    import grpc
    from oauth_proto import resource_pb2, resource_pb2_grpc
    channel = grpc.insecure_channel('34.71.162.16:50051')
    stub = resource_pb2_grpc.ResourceServiceStub(channel)
    print('GRPC: Getting resource')
    response = stub.GetResource(resource_pb2.ResourceRequest(),
        metadata=[
            ('authorization', "Bearer " + bearerToken),
        ]
    )
    print(f"GRPC: Retrieved username \"{response.username}\"")
    return response.username

if __name__ == "__main__":
    print(getUsername("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNjU1NjI4NTY4LCJpYXQiOjE2NTU2MjQ5NjgsImp0aSI6ImUzN2I5M2ZjOTg5MDQyMWNiOTczZThhZTdiMGIyM2Q1IiwidXNlcl9pZCI6MTcsInVzZXJuYW1lIjoiZGluZ3VzIn0.A6JcoGQJlqRXZamF3_mHcsQRyRg1ljhTu80kfvfvjKg"))