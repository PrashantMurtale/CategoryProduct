pipeline {
    agent any

    environment {
        GOOGLE_APPLICATION_CREDENTIALS = credentials('gcp-service-account') // Jenkins credential ID
        PROJECT_ID = 'springbootapp-gke'
        REPO = 'springboot-app'
        IMAGE_NAME = 'springboot-app'
        IMAGE_TAG = 'latest'
        REGION = 'us-central1'
        CLUSTER_NAME = 'springboot-gke'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/PrashantMurtale/CategoryProduct.git'
            }
        }

        stage('Build Spring Boot App') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Authenticate with GCP') {
            steps {
                sh """
                gcloud auth activate-service-account --key-file=${GOOGLE_APPLICATION_CREDENTIALS}
                gcloud auth configure-docker ${REGION}-docker.pkg.dev
                """
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                docker build -t ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO}/${IMAGE_NAME}:${IMAGE_TAG} .
                """
            }
        }

        stage('Push Docker Image') {
            steps {
                sh """
                docker push ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO}/${IMAGE_NAME}:${IMAGE_TAG}
                """
            }
        }

        stage('Deploy to GKE') {
            steps {
                sh """
                gcloud container clusters get-credentials ${CLUSTER_NAME} --region ${REGION} --project ${PROJECT_ID}
                kubectl apply -f k8s-manifests/deployment.yaml
                kubectl set image deployment/springboot-app springboot-app=${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO}/${IMAGE_NAME}:${IMAGE_TAG}
                kubectl rollout status deployment/springboot-app
                """
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
