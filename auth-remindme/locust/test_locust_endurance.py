from locust import HttpUser, TaskSet, task, constant
from locust import LoadTestShape

API_POST = "http://34.71.162.16:8000/api/token/"

data = {
    "username" : "kim123456",
    "password" : "hallo123yy"
}

class UserTasks(TaskSet):
    @task
    def get_resource(self):
        self.client.post(API_POST, json=data)

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
        {"duration": 120, "users": 40, "spawn_rate": 5},
        {"duration": 28800, "users": 40, "spawn_rate": 5},
        {"duration": 120, "users": 0, "spawn_rate": 5},
    ]

    def tick(self):
        run_time = self.get_run_time()

        for stage in self.stages:
            if run_time < stage["duration"]:
                tick_data = (stage["users"], stage["spawn_rate"])
                return tick_data

        return None