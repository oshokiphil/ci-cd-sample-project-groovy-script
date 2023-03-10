node
{
    def mavenHome = tool name: 'maven3.6.3'
    stage('SCM clone'){
        git credentialsId: 'gitcred', url: 'https://github.com/Oshokiphil-Devops/spring-boot-docker'
    }
    stage('Mavenbuild'){
        sh '${mavenHome}/bin/mvn clean package'
    }
    stage('QualityReport') {
        sh '${mavenHome}/bin/mvn sonar:sonar' 
    }
      stage('NexusUpload') {
        sh '${mavenHome}/bin/mvn deploy' 
    }
       stage('Builddockerimage') {
        sh 'docker build -t oshokiphil/spring-boot-mongo . ' 
    }
    stage('Pushimagereg'){
        withCredentials([string(credentialsId: 'dockercred', variable: 'dockercred')]) {
    sh 'docker login -u oshokiphil -p ${dockercred}'
}
        
        sh 'docker push oshokiphil/spring-boot-mongo'
    }
         stage('removedockerimages') {
        sh 'docker rmi $(docker images -q)' 
    }
       stage('deployapptok8s') {
        sh 'kubectl apply -f springapp.yml' 
    }
}
