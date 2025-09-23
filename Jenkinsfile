pipeline {
    agent any

    tools {
        jdk 'jdk21'      // must match Global Tool Config name
        maven 'M3'       // must match Global Tool Config name
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
                git branch: 'main', url: 'https://github.com/PrashantMurtale/CategoryProduct.git'
            }
        }

        stage('Build App') {
            steps {
                sh "mvn clean package -DskipTests"
            }
        }
    }

    post {
        always {
            echo "Pipeline finished."
        }
    }
}
