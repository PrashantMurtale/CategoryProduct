pipeline {
    agent any

    environment {
        PROJECT_ID = 'springbootapp-gke'
        CLUSTER_NAME = 'gke-cluster'
        ZONE = 'us-central1-a'
        IMAGE_NAME = 'springboot-app'
        IMAGE_TAG = 'latest'
        GCP_CREDENTIALS = credentials('gcp-service-account') // Jenkins credential ID
        DOCKER_REGISTRY = 'gcr.io' // or your Docker Hub registry
        SONARQUBE_SERVER = 'SonarQube' // Jenkins SonarQube server name
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/<your-username>/<repo>.git'
            }
        }

        stage('Set up GCP') {
            steps {
                script {
                    // Activate GCP service account
                    writeFile file: 'jenkins-sa.json', text: GCP_CREDENTIALS
                    sh 'gcloud auth activate-service-account --key-file=jenkins-sa.json'
                    sh "gcloud config set project ${PROJECT_ID}"
                    sh "gcloud container clusters get-credentials ${CLUSTER_NAME} --zone ${ZONE} --project ${PROJECT_ID}"
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv(SONARQUBE_SERVER) {
                    sh './gradlew sonarqube' // or `mvn sonar:sonar` if Maven
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_REGISTRY}/${PROJECT_ID}/${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Push Docker Image') {
            steps {
                sh "docker push ${DOCKER_REGISTRY}/${PROJECT_ID}/${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }

        stage('Deploy to GKE') {
            steps {
                sh """
                    kubectl set image deployment/${IMAGE_NAME} ${IMAGE_NAME}=${DOCKER_REGISTRY}/${PROJECT_ID}/${IMAGE_NAME}:${IMAGE_TAG} --record
                    kubectl rollout status deployment/${IMAGE_NAME}
                """
            }
        }
    }

    post {
        always {
            sh 'rm -f jenkins-sa.json'
        }
    }
}
