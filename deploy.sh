#!/bin/bash

# 기존에 실행 중인 프로세스가 있다면 종료
pkill -f 'java -jar'

# 새로 빌드한 jar 파일로 애플리케이션 실행
nohup java -jar /home/gitlab-runner/app/demo.jar &

echo "Deployment completed successfully!"
