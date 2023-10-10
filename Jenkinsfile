pipeline {
    agent any
    // tools{
    //     maven 'Maven 3.8.4'
    // }
    stages{
        stage('Test'){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/shayandaneshvar/KBox']]])
                sh 'docker-compose -f docker-compose-db.yaml up -d'
                sh 'mvn clean test'
                sh 'docker-compose -f docker-compose-db.yaml down'
            }
        }
        stage('Build & Dockerize'){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/shayandaneshvar/KBox']]])
                sh 'mvn clean package docker:build -DskipTests -X'
                // or use docker build -t "" to build image from dockerfile
            }
        }

        stage('Push image to DockerHub'){ // alternatives are Netflix OSS, JFrog
            steps{
                script{
                    withCredentials([string(credentialsId: 'docker-pwd', variable: 'dockerpwd')]) {
                        sh 'docker login -u shayandaneshvar -p ${dockerpwd}'
                    }
                    sh 'docker tag kbox/manager:v1 shayandaneshvar/kbox:v1'
                    sh 'docker push shayandaneshvar/kbox:v1' // your image
                }
            }
        }

        stage('Deploy with Docker-Compose'){
            steps{
                script{
                    sh 'docker-compose -f docker-compose.yaml down'
                    sh 'docker-compose -f docker-compose.yaml up -d'
                }
            }
        }
        // stage('Deploy to k8s'){
        //     steps{
        //         script{
        //             kubernetesDeploy (configs: 'deploymentservice.yaml',kubeconfigId: 'k8sconfigpwd')
        //         }
        //     }
        // }
    }
}