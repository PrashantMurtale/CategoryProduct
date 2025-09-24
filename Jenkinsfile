pipeline {
    agent any

    environment {
        PROJECT_ID = 'springbootapp-gke'
        REPO = 'springboot-app'
        IMAGE_NAME   = "us-central1-a-docker.pkg.dev/springbootapp-gke/springboot-app/springboot-app"
        IMAGE_TAG = 'latest'
        REGION = 'us-central1'
        CLUSTER_NAME = 'gke-cluster'
    }

    stages {
        stage('Checkout') {
            steps {
                git(
                    branch: 'main',
                    credentialsId: 'github-ssh-cred',   // your GitHub SSH credential ID
                    url: 'git@github.com:PrashantMurtale/CategoryProduct.git'
                )
            }
        }

        stage('Set up GCP') {
            steps {
                withCredentials([file(credentialsId: 'gcp-service-account', variable: 'GOOGLE_APPLICATION_CREDENTIALS')]) {
                    script {
                        sh """
                            echo "Authenticating with GCP..."
                            gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS
                            gcloud config set project $PROJECT_ID
                        """
                    }
                }
            }
        }

        stage('Build with Maven') {
            steps {
                script {
                    sh """
                        echo "Building Spring Boot JAR..."
                        mvn clean package -DskipTests
                    """
                }
            }
        }

        stage('Docker Auth') {
            steps {
                withCredentials([file(credentialsId: 'gcp-service-account', variable: 'GOOGLE_APPLICATION_CREDENTIALS')]) {
                    sh """
                        gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS
                        gcloud auth configure-docker ${REGION}-docker.pkg.dev -q
                    """
                }
            }
        }
         stage('Build Docker Image') {
            steps {
                sh """
                    echo Building Docker image...
                    docker build -t springboot-app:latest .
                """
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    sh """
                        echo "Pushing Docker image to GCP Artifact Registry..."
                        docker push gcr.io/springbootapp-gke/springboot-app:latest
                    """
                }
            }
        }

        stage('Deploy to GKE') {
            steps {
                withCredentials([file(credentialsId: 'gcp-service-account', variable: 'GOOGLE_APPLICATION_CREDENTIALS')]) {
                    script {
                        sh """
                            echo "Getting GKE credentials..."
                            gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS
                            gcloud container clusters get-credentials $CLUSTER_NAME --region $REGION --project $PROJECT_ID
                            echo "Applying Kubernetes manifests..."
                            kubectl apply -f deployment.yml
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                sh 'echo "Pipeline finished - cleaning up..."'
            }
        }
        success {
            script {
                sh 'echo "✅ Deployment successful!"'
            }
        }
        failure {
            script {
                sh 'echo "❌ Deployment failed!"'
            }
        }
    }
}
