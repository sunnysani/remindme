DROP TABLE IF EXISTS schedule;

CREATE TABLE IF NOT EXISTS schedule (
  id int(11) NOT NULL AUTO_INCREMENT,
  activity_name varchar(255) NOT NULL,
  schedule_time int(100) NOT NULL,
  schedule_interval varchar(255) NOT NULL,
  username varchar(255) NOT NULL,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS job_info;

CREATE TABLE IF NOT EXISTS job_info (
  id int(11) NOT NULL AUTO_INCREMENT,
  job_name varchar(255) NOT NULL,
  job_group varchar(255) NOT NULL,
  job_status varchar(255) NOT NULL,
  job_class varchar(255) NOT NULL,
  cron_expression varchar(255) NOT NULL,
  description varchar(255) NOT NULL,
  cron_job BOOLEAN NOT NULL,
  schedule_id int(11) NOT NULL,
  base_job_name varchar(255) NULL,
  schedule_time int(100) NOT NULL,
  schedule_interval varchar(255) NOT NULL,
  username varchar(255) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (schedule_id)
  REFERENCES schedule(id)
);