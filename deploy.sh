#!/bin/bash

# 기존에 실행 중인 프로세스 종료
pkill -f 'java -jar'

# 새로 빌드한 JAR 파일로 애플리케이션 실행 (dev 프로필 적용)
nohup java -jar /home/gitlab-runner/app/demo.jar --spring.profiles.active=dev > /home/gitlab-runner/app/log.out 2>&1 &

echo "Deployment completed successfully with 'dev' profile!"