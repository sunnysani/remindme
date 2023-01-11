from locust import HttpUser, TaskSet, task, constant
from locust import LoadTestShape

API_GET = "http://34.71.162.16:8000/api/resource/"

class UserTasks(TaskSet):
    @task
    def get_resource(self):
        # isi dengan token yg baru
        self.SessionToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNjUzNjQ5MDUwLCJpYXQiOjE2NTM2NDU0NTAsImp0aSI6ImY4YjZhYzM5MDRkYzQwZGU5MmQzNGJiYjQxZTdlZDU0IiwidXNlcl9pZCI6MX0.R4-8KGtCNhbVJ228g1wtu3jqK13jiZKKAe4HMW9RDbo"
        self.client.get(API_GET, headers={"Authorization": "Bearer " + self.SessionToken})

class WebsiteUser(HttpUser):
    wait_time = constant(1)
    tasks = [UserTasks]


class StagesShape(LoadTestShape):
    """
    A simply load test shape class that has different user and spawn_rate at
    different stages.
    Keyword arguments:
        stages -- A list of dicts, each representing a stage with the following keys:
            duration -- When this many seconds pass the test is advanced to the next stage
            users -- Total user count
            spawn_rate -- Number of users to start/stop per second
            stop -- A boolean that can stop that test at a specific stage
        stop_at_end -- Can be set to stop once all stages have run.
    """

    stages = [
        {"duration": 300, "users": 500, "spawn_rate": 10},
        {"duration": 600, "users": 500, "spawn_rate": 10},
        {"duration": 300, "users": 0, "spawn_rate": 10},
    ]

    def tick(self):
        run_time = self.get_run_time()

        for stage in self.stages:
            if run_time < stage["duration"]:
                tick_data = (stage["users"], stage["spawn_rate"])
                return tick_data

        return None