pipeline {
  agent any
  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }
    stage('Build Docker Images into Minikube') {
      steps {
        powershell '& minikube -p minikube docker-env --shell powershell | Invoke-Expression; docker build -t docapp-backend:local -f backend/Dockerfile backend; docker build -t docapp-frontend:local -f frontend/Dockerfile frontend'
      }
    }
    stage('Deploy to Kubernetes') {
      steps {
        powershell 'kubectl apply -f k8s; kubectl rollout status deploy/backend --timeout=180s; kubectl rollout status deploy/frontend --timeout=180s; kubectl rollout status deploy/mysql --timeout=180s'
      }
    }
    stage('Cluster Sanity') {
      steps {
        powershell 'kubectl get pods -o wide; kubectl get svc -o wide; kubectl get ingress'
      }
    }
  }
}
