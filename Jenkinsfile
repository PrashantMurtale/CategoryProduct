pipeline {
    agent any

    tools {
        jdk 'jdk21'          // Configure this name in Jenkins Global Tool Config
        maven 'M3'           // Configure Maven installation in Jenkins as "M3"
    }

    environment {
        PROJECT_ID   = 'springbootapp-gke'
        IMAGE_NAME   = 'springboot-app'
        IMAGE_TAG    = 'latest'
        CLUSTER_NAME = 'gke-cluster'
        REGION       = 'us-central1'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', credentialsId: 'github-ssh-cred', url: 'git@github.com:PrashantMurtale/CategoryProduct.git'
            }
        }

        stage('Set up GCP') {
            steps {
                withCredentials([file(credentialsId: 'gcp-service-account', variable: 'GOOGLE_APPLICATION_CREDENTIALS')]) {
                    sh """
                        echo Authenticating with GCP...
                        gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS
                        gcloud config set project $PROJECT_ID
                        gcloud container clusters get-credentials $CLUSTER_NAME --region $REGION --project $PROJECT_ID
                    """
                }
            }
        }

        stage('Build App') {
            steps {
                script {
                    if (fileExists('mvnw')) {
                        sh "./mvnw clean package -DskipTests"
                    } else {
                        sh "mvn clean package -DskipTests"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    echo Building Docker image...
                    docker build -t gcr.io/$PROJECT_ID/$IMAGE_NAME:$IMAGE_TAG .
                """
            }
        }

        stage('Push Docker Image') {
            steps {
                sh """
                    echo Pushing Docker image to Artifact Registry...
                    gcloud auth configure-docker -q
                    docker push gcr.io/$PROJECT_ID/$IMAGE_NAME:$IMAGE_TAG
                """
            }
        }

        stage('Deploy to GKE') {
            steps {
                sh """
                    echo Applying Kubernetes manifests...
                    kubectl apply -f k8s/deployment.yaml
                    kubectl apply -f k8s/service.yaml
                """
            }
        }
    }

    post {
        success {
            echo "✅ Deployment successful!"
        }
        failure {
            echo "❌ Deployment failed!"
        }
    }
}
